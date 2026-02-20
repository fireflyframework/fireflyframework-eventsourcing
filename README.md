# Firefly Framework - Event Sourcing

[![Version](https://img.shields.io/badge/version-26.02.06-blue.svg)](pom.xml)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

A reactive event sourcing library for Spring Boot that stores domain events as the source of truth, enabling full audit trails, temporal queries, and event-driven architectures. Built on R2DBC and Project Reactor.

## How It Works

### Command Flow (Write)

```
Command --> Aggregate --> [validate] --> Event(s) --> EventStore.appendEvents()
                                                         |
                                              +----------+-----------+
                                              |                      |
                                       events table            event_outbox
                                     (BIGSERIAL seq)          (if publisher
                                                                configured)
```

### Read Flow (Query)

```
EventStore.loadEventStream() --> StoredEventEnvelope[] --> aggregate.loadFromHistory()
                                                               |
                                                        Aggregate (current state)
```

## Version Semantics

| State                  | `AggregateRoot.version` | `expectedVersion` for `appendEvents` |
|------------------------|:-----------------------:|:------------------------------------:|
| New (no events yet)    | -1                      | -1                                   |
| After 1st event        | 0                       | 0 (for next append)                  |
| After Nth event        | N-1                     | N-1 (for next append)                |

The aggregate version starts at **-1** and increments with each event applied via `applyChange()`. When calling `appendEvents`, pass the aggregate's current version as `expectedVersion` for optimistic concurrency control.

## Features

**Core**
- `AggregateRoot` base class with reflection-based event handler dispatch
- Reactive `EventStore` interface backed by R2DBC (PostgreSQL, H2, MySQL)
- Optimistic concurrency control via aggregate versioning
- `@DomainEvent` annotation for declarative event type registration (bridges to `@JsonTypeName`)
- `AbstractDomainEvent` with builder pattern and metadata helpers (correlationId, causationId, userId, source)
- `StoredEventEnvelope` wraps domain events with storage metadata (global sequence, created timestamp)
- `EventStream` with query helpers (`getEventsFromVersion`, `getEventsInRange`, `isEmpty`, `size`)

**Persistence**
- Flyway-managed schema with 8 migrations (V1-V8)
- `BIGSERIAL` global sequence assigned by the database -- INSERT excludes `global_sequence`
- `TEXT` columns for `event_data` and `metadata` (database-agnostic, not JSONB)
- Snapshot store with UPSERT semantics -- PK is `(aggregate_id, aggregate_type)`, one snapshot per aggregate
- Transactional Outbox pattern for reliable event publishing with exponential backoff retry

**Operations**
- `@EventSourcingTransactional` with configurable propagation, isolation, retry, and timeout
- Auto-configuration chain (9 configuration classes with conditional bean creation)
- Health indicators: EventStore, Outbox, Snapshot, Projection
- Micrometer metrics via `EventStoreMetrics` (timers, counters, gauges)
- Structured logging with 16 MDC keys and reactive context propagation
- Circuit breakers (eventStore, outbox, projection) via Resilience4j (off by default)
- Multi-tenancy via `TenantContext` (off by default)
- Event upcasting for schema evolution via `EventUpcaster` interface

## Requirements

- Java 21+
- Spring Boot 3.x
- Maven 3.9+
- PostgreSQL (recommended) or any R2DBC-compatible database

## Installation

```xml
<dependency>
    <groupId>org.fireflyframework</groupId>
    <artifactId>fireflyframework-eventsourcing</artifactId>
    <version>26.02.06</version>
</dependency>
```

## Quick Start

### 1. Define Domain Events

```java
@DomainEvent("order.placed")
@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent extends AbstractDomainEvent {
    private String productId;
    private int quantity;
    private BigDecimal totalPrice;
}
```

### 2. Create an Aggregate

```java
public class Order extends AggregateRoot {

    private String productId;
    private int quantity;
    private BigDecimal totalPrice;

    // Constructor for loading from event store
    public Order(UUID id) {
        super(id, "Order");
    }

    // Constructor for creating a new order (command)
    public Order(UUID id, String productId, int quantity, BigDecimal totalPrice) {
        super(id, "Order");
        applyChange(OrderPlacedEvent.builder()
                .aggregateId(id)
                .productId(productId)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .build());
    }

    // Event handler -- updates state, no validation
    private void on(OrderPlacedEvent event) {
        this.productId = event.getProductId();
        this.quantity = event.getQuantity();
        this.totalPrice = event.getTotalPrice();
    }
}
```

### 3. Persist and Load

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final EventStore eventStore;

    public Mono<Order> placeOrder(String productId, int qty, BigDecimal price) {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, productId, qty, price);

        return eventStore.appendEvents(
                orderId, "Order", order.getUncommittedEvents(), -1L  // -1 = new aggregate
            )
            .doOnSuccess(stream -> order.markEventsAsCommitted())
            .thenReturn(order);
    }

    public Mono<Order> getOrder(UUID orderId) {
        return eventStore.loadEventStream(orderId, "Order")
            .map(stream -> {
                Order order = new Order(orderId);
                order.loadFromHistory(stream.getEvents());
                return order;
            });
    }
}
```

## Architecture

```
+--------------------------------------------------+
|                Application Layer                  |
|  Services, Controllers, Command Handlers          |
+--------------------------------------------------+
          |                          |
          v                          v
+-------------------+    +---------------------+
|   Domain Layer    |    | Infrastructure Layer |
|                   |    |                      |
| AggregateRoot     |    | R2dbcEventStore      |
| Event interface   |    | R2dbcSnapshotStore   |
| AbstractDomainEvent|   | EventTypeRegistry    |
| @DomainEvent      |    | EventOutboxService   |
| StoredEventEnvelope|   | EventSourcingPublisher|
| EventStream       |    +---------------------+
+-------------------+             |
                                  v
                        +-------------------+
                        |  PostgreSQL (R2DBC)|
                        |                   |
                        | events            |
                        | snapshots         |
                        | event_outbox      |
                        | projection_positions|
                        +-------------------+
```

## Database Schema

The library ships with 8 Flyway migrations. Key tables:

| Table | Purpose | PK |
|-------|---------|-----|
| `events` | Append-only event log | `event_id` (UUID) |
| `snapshots` | Aggregate state cache | `(aggregate_id, aggregate_type)` |
| `event_outbox` | Transactional outbox for publishing | `outbox_id` (UUID) |
| `projection_positions` | Projection checkpoint tracking | `projection_name` |

The `events` table uses `BIGSERIAL` for `global_sequence` -- the database auto-assigns sequence numbers. The INSERT statement does not include `global_sequence`. Columns `event_data` and `metadata` are `TEXT`, not JSONB.

See [Database Schema Reference](docs/database-schema.md) for full details.

## Configuration

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/mydb
    username: user
    password: pass

firefly:
  eventsourcing:
    enabled: true
    event-scan-packages: "com.example.myapp"
    store:
      type: r2dbc
      batch-size: 100
    snapshot:
      enabled: true
      threshold: 50
    publisher:
      enabled: true
```

See [Configuration Reference](docs/configuration.md) for all properties and defaults.

## Documentation

| Document | Description |
|----------|-------------|
| [Event Sourcing Explained](docs/event-sourcing-explained.md) | Conceptual introduction and comparison with CRUD |
| [Quick Start](docs/quick-start.md) | Step-by-step setup guide |
| [Architecture](docs/architecture.md) | System layers, auto-configuration, event flows |
| [API Reference](docs/api-reference.md) | Complete interface and class documentation |
| [Configuration](docs/configuration.md) | All properties with defaults |
| [Database Schema](docs/database-schema.md) | Flyway migrations, tables, indexes, triggers |
| [Testing](docs/testing.md) | Unit, integration, and projection testing |
| [Account Ledger Tutorial](docs/tutorial-account-ledger.md) | Complete working example |
| [Optional Enhancements](docs/optional-enhancements.md) | Circuit breakers, metrics, multi-tenancy, upcasting |

## License

Copyright 2024-2026 Firefly Software Solutions Inc. Licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
