# Firefly Framework - Event Sourcing

[![CI](https://github.com/fireflyframework/fireflyframework-eventsourcing/actions/workflows/ci.yml/badge.svg)](https://github.com/fireflyframework/fireflyframework-eventsourcing/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21%2B-orange.svg)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)

> Production-ready event sourcing library for reactive Spring Boot microservices. Provides aggregate roots, R2DBC event store, snapshots, projections, and EDA integration.

---

## Table of Contents

- [Overview](#overview)
- [How It Works](#how-it-works)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Architecture](#architecture)
- [Configuration](#configuration)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)

## Overview

Firefly Framework Event Sourcing provides a comprehensive implementation of the Event Sourcing pattern for reactive Spring Boot microservices. Instead of persisting current state to a database, it records every state change as an immutable event. The current state of any entity is derived by replaying its event history.

This approach enables:

- **Complete audit trails** for regulatory compliance
- **Temporal queries** ("what was the account balance on March 15th?")
- **Event replay** for testing new business rules against historical data
- **Reliable integration** via domain event publishing to message brokers
- **Optimistic concurrency** via version-based conflict detection

## How It Works

### Writing: Command -> Events -> Database

```
1. Load aggregate from events    EventStore.loadEventStream(id, type)
2. Reconstruct state             AggregateRoot.loadFromHistory(events)
3. Execute business logic        aggregate.deposit(amount)
4. Persist new events            EventStore.appendEvents(id, type, events, expectedVersion)
5. Publish to message broker     EventSourcingPublisher.publishEvents() (async, after commit)
```

### Reading: Events -> Aggregate State

```
Database (events table)              In-Memory Aggregate
┌──────────────────────────┐         ┌─────────────────────┐
│ AccountCreated(1000)     │──apply──│ balance = 1000      │
│ MoneyDeposited(250)      │──apply──│ balance = 1250      │
│ MoneyWithdrawn(100)      │──apply──│ balance = 1150      │
└──────────────────────────┘         └─────────────────────┘
```

### Version Semantics

Aggregates start at version `-1` (no events). Each `applyChange()` increments the version:

| State | Version | Meaning |
|-------|---------|---------|
| New aggregate (no events) | `-1` | Non-existent in database |
| After 1st event applied | `0` | First event stored |
| After Nth event applied | `N-1` | N events total |

When persisting a new aggregate, pass `expectedVersion = -1` to `appendEvents()`. For subsequent updates, use the aggregate's current version.

## Features

### Core

- `AggregateRoot` base class with reflection-based event handler dispatch
- R2DBC event store for PostgreSQL with reactive transaction management
- Optimistic concurrency control via aggregate versioning
- Global event ordering via PostgreSQL `BIGSERIAL` auto-increment
- Snapshot store for performance optimization (configurable event threshold)
- Projection framework with `ProjectionService` for building read models

### Annotations

- `@DomainEvent` for declarative event type registration (bridges to `@JsonTypeName`)
- `@EventSourcingTransactional` for ACID-compliant event persistence with automatic retry on concurrency conflicts

### Event Type Discovery

- `EventTypeRegistry` automatically scans the classpath on startup for `Event` implementations annotated with `@JsonTypeName`
- Registers discovered types with Jackson's `ObjectMapper` for polymorphic deserialization
- Configurable scan packages via `firefly.eventsourcing.event-scan-packages`

### Reliability

- Transactional outbox pattern for reliable event publishing
- Circuit breaker configuration for resilient event operations
- Event upcasting service for schema evolution

### Observability

- Event store and projection health indicators (Spring Actuator)
- Micrometer metrics for event store and snapshot operations
- OpenTelemetry tracing across event processing pipelines
- Structured logging context (MDC) for aggregate ID, type, version, and event type

### Multi-Tenancy

- Tenant context isolation with `TenantContext`
- Tenant-scoped event queries

### Auto-Configuration

The library provides Spring Boot auto-configuration with ordered bean creation:

1. **R2dbcBeansAutoConfiguration** imports Spring R2DBC infrastructure (`DatabaseClient`, `R2dbcEntityTemplate`)
2. **EventStoreAutoConfiguration** creates `R2dbcEventStore` (requires R2DBC beans via `@ConditionalOnBean`)
3. **SnapshotAutoConfiguration** creates snapshot beans when enabled
4. **EventSourcingAutoConfiguration** creates publisher, transaction aspect, outbox, metrics, and health beans

Each configuration class independently registers `EventSourcingProperties` via `@EnableConfigurationProperties`.

## Requirements

- Java 21+
- Spring Boot 3.x
- Maven 3.9+
- PostgreSQL database (for event store)

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
@DomainEvent("account.created")
public record AccountCreatedEvent(
    UUID aggregateId,
    String accountNumber,
    BigDecimal initialBalance
) implements Event {
    @Override public String getEventType() { return "account.created"; }
    @Override public UUID getAggregateId() { return aggregateId; }
}
```

### 2. Create an Aggregate

```java
public class Account extends AggregateRoot {
    private String accountNumber;
    private BigDecimal balance;

    // Constructor for new aggregates
    public Account(UUID id, String accountNumber, BigDecimal initialBalance) {
        super(id, "Account");
        applyChange(new AccountCreatedEvent(id, accountNumber, initialBalance));
    }

    // Constructor for loading from event store
    public Account(UUID id) {
        super(id, "Account");
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be positive");
        applyChange(new MoneyDepositedEvent(getId(), amount));
    }

    // Event handlers (private, named "on", single event parameter)
    private void on(AccountCreatedEvent event) {
        this.accountNumber = event.accountNumber();
        this.balance = event.initialBalance();
    }

    private void on(MoneyDepositedEvent event) {
        this.balance = this.balance.add(event.amount());
    }
}
```

### 3. Persist and Load

```java
@Service
@RequiredArgsConstructor
public class AccountService {
    private final EventStore eventStore;

    public Mono<Account> createAccount(String number, BigDecimal balance) {
        UUID id = UUID.randomUUID();
        Account account = new Account(id, number, balance);
        return eventStore.appendEvents(id, "Account",
                    account.getUncommittedEvents(), -1L)  // -1 = new aggregate
                .doOnSuccess(s -> account.markEventsAsCommitted())
                .thenReturn(account);
    }

    public Mono<Account> loadAccount(UUID id) {
        return eventStore.loadEventStream(id, "Account")
                .map(stream -> {
                    Account account = new Account(id);
                    account.loadFromHistory(stream.getEvents());
                    return account;
                });
    }
}
```

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Application Layer                            │
│  Services       Controllers      Command Handlers              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Domain Layer                                │
│  AggregateRoot    Event           @DomainEvent                 │
│  applyChange()    loadFromHistory()   Business Logic           │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                Infrastructure Layer                             │
│  R2dbcEventStore  R2dbcSnapshotStore  EventSourcingPublisher   │
│  EventTypeRegistry  TransactionalAspect  Outbox               │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   External Systems                              │
│  PostgreSQL       Kafka / RabbitMQ    Redis                    │
│  (events table)   (event publishing)  (snapshot cache)         │
└─────────────────────────────────────────────────────────────────┘
```

### Database Schema

The `events` table uses PostgreSQL `BIGSERIAL` for global ordering. The `global_sequence` column is auto-populated by the database; the INSERT statement deliberately excludes it.

```sql
CREATE TABLE events (
    event_id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_version BIGINT NOT NULL,
    global_sequence BIGSERIAL UNIQUE,         -- Auto-populated by PostgreSQL
    event_type VARCHAR(255) NOT NULL,
    event_data JSONB NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(aggregate_id, aggregate_version),
    CHECK (aggregate_version >= 0)
);
```

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
    event-scan-packages: org.fireflyframework,com.myapp  # Classpath scan for event types
    store:
      type: r2dbc
      batch-size: 100
    snapshot:
      enabled: true
      threshold: 50          # Create snapshot every 50 events
    publisher:
      enabled: true
      type: AUTO             # Auto-detect Kafka/RabbitMQ
```

See [Configuration Reference](docs/configuration.md) for all properties.

## Documentation

Detailed documentation is available in the [docs/](docs/) directory:

- [Quick Start Guide](docs/quick-start.md) - Build your first event-sourced app in 5 minutes
- [Architecture Overview](docs/architecture.md) - System design, data flow, and component interactions
- [API Reference](docs/api-reference.md) - Complete public API documentation
- [Configuration Reference](docs/configuration.md) - All configuration properties with examples
- [Database Schema](docs/database-schema.md) - Table definitions, indexes, queries, and migrations
- [Testing Guide](docs/testing.md) - Unit, integration, and performance testing with Testcontainers
- [Event Sourcing Explained](docs/event-sourcing-explained.md) - Conceptual introduction
- [Account Ledger Tutorial](docs/tutorial-account-ledger.md) - Complete working example
- [Optional Enhancements](docs/optional-enhancements.md) - Circuit breakers, tracing, multi-tenancy

## Contributing

Contributions are welcome. Please read the [CONTRIBUTING.md](CONTRIBUTING.md) guide for details on our code of conduct, development process, and how to submit pull requests.

## License

Copyright 2024-2026 Firefly Software Solutions Inc.

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.
