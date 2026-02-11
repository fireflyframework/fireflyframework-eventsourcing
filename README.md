# Firefly Framework - Event Sourcing

[![CI](https://github.com/fireflyframework/fireflyframework-eventsourcing/actions/workflows/ci.yml/badge.svg)](https://github.com/fireflyframework/fireflyframework-eventsourcing/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21%2B-orange.svg)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)

> Event sourcing library with aggregate roots, R2DBC event store, snapshots, projections, and EDA integration.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)

## Overview

Firefly Framework Event Sourcing provides a comprehensive implementation of the Event Sourcing pattern for reactive Spring Boot microservices. It persists domain state as an ordered sequence of events rather than current state, enabling complete audit trails, temporal queries, and event replay capabilities.

The library includes `AggregateRoot` as the base class for event-sourced entities, an R2DBC-backed event store for PostgreSQL, configurable snapshot strategies for performance optimization, and a projection framework for building read models from event streams. It integrates with the EDA module for publishing domain events to external consumers.

Additional features include event upcasting for schema evolution, outbox pattern support for reliable event publishing, multi-tenancy configuration, circuit breaker resilience, and OpenTelemetry tracing across event processing pipelines.

## Features

- `AggregateRoot` base class with event application and replay
- R2DBC event store implementation for PostgreSQL
- Configurable snapshot store with automatic snapshotting strategies
- Projection framework with `ProjectionService` for building read models
- `@DomainEvent` annotation for event type registration
- `@EventSourcingTransactional` annotation for transactional event persistence
- Event type registry for serialization/deserialization
- Event upcasting service for schema evolution
- Outbox pattern for reliable event publishing
- Multi-tenancy support with tenant context isolation
- Circuit breaker configuration for resilient event operations
- OpenTelemetry tracing for event processing pipelines
- Event store and projection health indicators
- Metrics collection for event store and snapshot operations
- Structured logging context for event sourcing operations
- Jackson JSON configuration for event serialization

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
    <version>26.02.01</version>
</dependency>
```

## Quick Start

```java
import org.fireflyframework.eventsourcing.aggregate.AggregateRoot;
import org.fireflyframework.eventsourcing.annotation.DomainEvent;

public class Account extends AggregateRoot {

    private BigDecimal balance = BigDecimal.ZERO;

    public void deposit(BigDecimal amount) {
        apply(new MoneyDepositedEvent(getId(), amount));
    }

    // Event handler - called during apply and replay
    private void on(MoneyDepositedEvent event) {
        this.balance = this.balance.add(event.getAmount());
    }
}

@DomainEvent(type = "money-deposited")
public record MoneyDepositedEvent(String accountId, BigDecimal amount) {}
```

## Configuration

```yaml
firefly:
  eventsourcing:
    event-store:
      schema: event_store
    snapshot:
      enabled: true
      threshold: 100
    projection:
      enabled: true
    multi-tenancy:
      enabled: false

spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/eventstore
```

## Documentation

Additional documentation is available in the [docs/](docs/) directory:

- [Quick Start](docs/quick-start.md)
- [Architecture](docs/architecture.md)
- [Configuration](docs/configuration.md)
- [Api Reference](docs/api-reference.md)
- [Event Sourcing Explained](docs/event-sourcing-explained.md)
- [Database Schema](docs/database-schema.md)
- [Testing](docs/testing.md)
- [Tutorial Account Ledger](docs/tutorial-account-ledger.md)
- [Optional Enhancements](docs/optional-enhancements.md)

## Contributing

Contributions are welcome. Please read the [CONTRIBUTING.md](CONTRIBUTING.md) guide for details on our code of conduct, development process, and how to submit pull requests.

## License

Copyright 2024-2026 Firefly Software Solutions Inc.

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.
