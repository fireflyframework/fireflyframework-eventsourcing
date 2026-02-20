# Firefly Event Sourcing Library Documentation 📚

Welcome to the comprehensive documentation for the Firefly Event Sourcing Library! Whether you're new to event sourcing or looking to implement it in production, we've got you covered.

## 🎯 **Choose Your Learning Path**

### 🌱 **New to Event Sourcing?**
**Start here to understand the fundamentals:**

1. **[Account Ledger Tutorial](./tutorial-account-ledger.md)** 🏦 - *Start here!* Complete guide with production-ready code
2. **[Event Sourcing Explained](./event-sourcing-explained.md)** 🎓 - What it is, why it matters, when to use it
3. **[Quick Start Guide](./quick-start.md)** ⚡ - Build your first event-sourced app in 5 minutes
4. **[Improved Developer Experience](./examples/improved-developer-experience.md)** 🎨 - Learn AbstractDomainEvent and enhanced patterns
5. **[Testing Guide](./testing.md)** 🧪 - Learn to test event-sourced systems

### 💪 **Already Know Event Sourcing?**
**Jump to implementation details:**

1. **[Quick Start Guide](./quick-start.md)** ⚡ - Get coding immediately
2. **[Account Ledger Tutorial](./tutorial-account-ledger.md)** 🏦 - See all patterns in action
3. **[Architecture Overview](./architecture.md)** 🏗️ - Understand our design decisions
4. **[API Reference](./api-reference.md)** 📖 - Detailed technical documentation
5. **[Configuration Reference](./configuration.md)** ⚙️ - Production configuration options

## 🚀 **Why This Library?**

### **Built for Financial Services**
- 🏦 **Regulatory Compliance** - Complete audit trails for regulatory requirements
- 🔒 **Optimistic Locking** - Handle concurrent transactions safely
- 📊 **Rich Analytics** - Query transaction patterns and fraud detection
- ⏰ **Temporal Queries** - "What was the account balance on March 15th?"

### **Production-Ready Architecture**
- ⚡ **Reactive Programming** - Handle thousands of concurrent operations
- 🗄️ **PostgreSQL Optimized** - JSONB storage with performance indexing
- 📊 **Built-in Monitoring** - Metrics, health checks, distributed tracing
- 🧪 **Comprehensive Testing** - Testcontainers integration for realistic testing

### **Developer Experience**
- 🎆 **Spring Boot Auto-Configuration** - Zero configuration setup
- 📚 **Extensive Documentation** - From concepts to production deployment
- 🛠️ **Rich Tooling** - Database migrations, testing utilities, examples
- 🔄 **Event Replay** - Test new business rules against historical data

## Additional Documentation
- [**Architecture Overview**](./architecture.md) - System design, components, and data flow
- [**API Reference**](./api-reference.md) - Complete public API documentation
- [**Database Schema**](./database-schema.md) - Table definitions, indexes, and queries
- [**Configuration Reference**](./configuration.md) - All configuration properties
- [**Testing Guide**](./testing.md) - Unit, integration, and performance testing
- [**Optional Enhancements**](./optional-enhancements.md) - Circuit breakers, tracing, multi-tenancy
- [**Examples**](./examples/) - Banking examples and developer experience patterns

## Library Overview

The Firefly Event Sourcing Library provides:

- **🚀 Reactive Architecture**: Built on Project Reactor for non-blocking operations
- **📦 Event Store Abstraction**: Pluggable implementations (R2DBC primary)
- **🏗️ Aggregate Framework**: Base classes for domain-driven design
- **📸 Snapshot Support**: Automatic performance optimization
- **🔄 EDA Integration**: Seamless message publishing
- **🗄️ R2DBC Integration**: Leverages fireflyframework-r2dbc utilities
- **🔧 Auto-Configuration**: Spring Boot ready

## Getting Started

### 1. Add Dependency

```xml
<dependency>
    <groupId>org.fireflyframework</groupId>
    <artifactId>fireflyframework-eventsourcing</artifactId>
    <version>26.02.06</version>
</dependency>
```

### 2. Minimal Configuration

```yaml
firefly:
  eventsourcing:
    enabled: true
    store:
      type: r2dbc
```

### 3. Create Your First Aggregate

```java
public class Account extends AggregateRoot {
    private String accountNumber;
    private BigDecimal balance;
    
    public Account(UUID id, String accountNumber, BigDecimal initialBalance) {
        super(id, "Account");
        applyChange(new AccountCreatedEvent(id, accountNumber, initialBalance));
    }
    
    private void on(AccountCreatedEvent event) {
        this.accountNumber = event.accountNumber();
        this.balance = event.initialBalance();
    }
}
```

## Key Components

### Core Packages

- `org.fireflyframework.eventsourcing.domain` - Core domain abstractions
- `org.fireflyframework.eventsourcing.aggregate` - Aggregate root implementation
- `org.fireflyframework.eventsourcing.store` - Event persistence layer
- `org.fireflyframework.eventsourcing.snapshot` - Snapshot management
- `org.fireflyframework.eventsourcing.publisher` - Event publishing
- `org.fireflyframework.eventsourcing.config` - Configuration and auto-setup

### Primary Interfaces

- **EventStore** - Event persistence and retrieval
- **Event** - Domain event abstraction  
- **AggregateRoot** - Base class for aggregates
- **SnapshotStore** - Snapshot persistence
- **EventSourcingPublisher** - Event publishing to message buses

## System Requirements

- Java 21+
- Spring Boot 3.5+
- Project Reactor
- R2DBC compatible database (PostgreSQL, MySQL, H2)
- fireflyframework-r2dbc for database operations

## Production Readiness

This library has been designed for production use in the Firefly Framework:

- ✅ **Transaction Safety** - ACID compliance for event persistence
- ✅ **Optimistic Locking** - Concurrency control with version checking
- ✅ **Reactive Streams** - Backpressure-aware processing
- ✅ **Health Monitoring** - Built-in health checks and metrics
- ✅ **Error Handling** - Comprehensive exception hierarchy
- ✅ **Performance** - Batching, caching, and optimization features

## Support & Contributing

For questions, issues, or contributions, please refer to the Firefly development team.

## License

Copyright 2024-2026 Firefly Software Solutions Inc. Licensed under the Apache License, Version 2.0.