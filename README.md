# Firefly Event Sourcing Library

[![CI](https://github.com/fireflyframework/fireflyframework-eventsourcing/actions/workflows/ci.yml/badge.svg)](https://github.com/fireflyframework/fireflyframework-eventsourcing/actions/workflows/ci.yml)

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)]()

A **production-ready** Spring Boot library for implementing Event Sourcing with reactive programming, designed for high-scale financial and enterprise applications.

---

## 📖 Table of Contents

- [What is Event Sourcing?](#what-is-event-sourcing)
- [Why Use This Library?](#why-use-this-library)
- [Quick Start](#quick-start)
- [Core Concepts](#core-concepts)
- [Complete Example: Account Ledger](#complete-example-account-ledger)
- [Architecture](#architecture)
- [Configuration](#configuration)
- [Testing](#testing)
- [Documentation](#documentation)
- [License](#license)

---

## What is Event Sourcing?

### The Core Idea (Explained Simply)

Imagine you're tracking your bank account. There are two ways to do this:

**📝 Traditional Way (CRUD):**
You have a notebook with just one line: "Current Balance: $900"

**Problem:** If someone asks "How did you get $900?", you can't answer. The history is lost.

**📚 Event Sourcing Way:**
You have a ledger with every transaction:
```
Jan 15, 10:00 AM - Opened account with $1,000
Jan 15, 2:30 PM  - Withdrew $100 at ATM Main St
Current Balance: $900 (calculated from events)
```

**Benefit:** You can answer ANY question about your account's history!

### Technical Comparison

<table>
<tr>
<th>Traditional CRUD</th>
<th>Event Sourcing</th>
</tr>
<tr>
<td>

**What you store:**
```sql
-- Just current state
UPDATE accounts
SET balance = 900
WHERE id = 'acc-123';
```

**❌ Lost Information:**
- How did it change?
- When did it change?
- Who made the change?
- Why was it changed?

**What you can answer:**
- "What is the current balance?" ✅

**What you CANNOT answer:**
- "What was the balance yesterday?" ❌
- "Who withdrew money last week?" ❌
- "How many deposits this month?" ❌

</td>
<td>

**What you store:**
```json
[
  {
    "type": "account.opened",
    "balance": 1000,
    "timestamp": "2025-01-15T10:00:00Z",
    "userId": "user-123"
  },
  {
    "type": "money.withdrawn",
    "amount": 100,
    "source": "ATM Main St",
    "timestamp": "2025-01-15T14:30:00Z",
    "userId": "user-123"
  }
]
```

**✅ Complete Information:**
- Full transaction history
- Who, what, when, where, why
- Time travel capabilities
- Regulatory compliance ready

**What you can answer:**
- "What is the current balance?" ✅
- "What was the balance yesterday?" ✅
- "Who withdrew money last week?" ✅
- "How many deposits this month?" ✅
- "Show me all ATM withdrawals" ✅

</td>
</tr>
</table>

### Real-World Analogy

Think of Event Sourcing like **Git for your data**:

- **Git** doesn't just store your current code - it stores every commit (event)
- You can see the **full history** of changes
- You can **time travel** to any previous state
- You can **replay** changes to understand how you got to the current state
- You can **branch** and experiment without losing history

Event Sourcing does the same for your business data!

### When to Use Event Sourcing (Decision Guide)

Ask yourself these questions:

#### ✅ Use Event Sourcing if you answer YES to any of these:

1. **"Do I need to know WHAT happened, not just the current state?"**
   - Example: Financial - "Show me all transactions for this account"
   - Example: Healthcare - "What treatments did this patient receive?"

2. **"Do I need to know WHEN something happened?"**
   - Example: Legal - "What was the contract status on March 15th?"
   - Example: Compliance - "Prove this change happened before the deadline"

3. **"Do I need to know WHO made changes?"**
   - Example: Audit - "Who approved this transaction?"
   - Example: Security - "Who accessed this sensitive data?"

4. **"Do I need to know WHY something changed?"**
   - Example: Fraud detection - "Why was this account frozen?"
   - Example: Debugging - "What caused this balance discrepancy?"

5. **"Do I need to replay or undo changes?"**
   - Example: Testing - "Replay production events in test environment"
   - Example: Recovery - "Undo the last 10 transactions"

#### ❌ Don't Use Event Sourcing if:

1. **"I just need basic CRUD operations"**
   - Example: Simple contact list, basic settings
   - Better choice: Traditional database with timestamps

2. **"I don't care about history"**
   - Example: Current weather data, cache data
   - Better choice: Key-value store or simple tables

3. **"My team has no experience with event sourcing"**
   - Risk: Steep learning curve, potential mistakes
   - Better choice: Start with traditional approach, migrate later if needed

4. **"I need simple, fast queries on current state only"**
   - Example: "Show me all active users" (no history needed)
   - Better choice: Traditional database with indexes

### Real-World Use Cases

**✅ Financial Systems (Perfect Fit):**
```
Question: "What was the account balance on December 31st for tax purposes?"
Event Sourcing: Replay all events up to Dec 31 → Exact balance ✅
Traditional DB: "We only have current balance" ❌
```

**✅ E-commerce (Perfect Fit):**
```
Question: "This customer claims they never received a refund. Prove it."
Event Sourcing: Show RefundProcessedEvent with timestamp, amount, method ✅
Traditional DB: "Order status shows 'Refunded' but no details" ❌
```

**❌ Simple Blog (Not a Good Fit):**
```
Question: "Show me all published blog posts"
Event Sourcing: Replay all PostCreated, PostPublished events → Overkill ❌
Traditional DB: SELECT * FROM posts WHERE status='published' → Simple ✅
```

---

## Why Use This Library?

### 🚀 Production-Ready Features

- **✅ Complete Event Sourcing Framework** - Aggregates, events, snapshots, projections
- **✅ Reactive & Non-Blocking** - Built on Project Reactor and R2DBC
- **✅ PostgreSQL Optimized** - JSONB storage, efficient indexing
- **✅ Spring Boot Auto-Configuration** - Zero-configuration setup
- **✅ Transactional Outbox Pattern** - Reliable event publishing
- **✅ Optimistic Locking** - Concurrency conflict detection
- **✅ Snapshot Support** - Performance optimization for large event streams
- **✅ Distributed Tracing** - OpenTelemetry integration
- **✅ Circuit Breakers** - Resilience4j integration
- **✅ Multi-Tenancy** - Built-in tenant isolation
- **✅ Comprehensive Testing** - 108 tests, Testcontainers support

### 📚 Learning Resources

**New to Event Sourcing?**
👉 **[Complete Tutorial: Building an Account Ledger](./docs/tutorial-account-ledger.md)**

**Already Familiar?**
- [Event Sourcing Explained](./docs/event-sourcing-explained.md) - Deep dive into concepts
- [Architecture Overview](./docs/architecture.md) - System design
- [API Reference](./docs/api-reference.md) - Complete API documentation

---

## Quick Start

### 1. Add Dependency

```xml
<dependency>
    <groupId>org.fireflyframework</groupId>
    <artifactId>fireflyframework-eventsourcing</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. Configure Database

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/eventstore
    username: postgres
    password: postgres

firefly:
  eventsourcing:
    enabled: true
    snapshot:
      enabled: true
      threshold: 50  # Create snapshot every 50 events
```

### 3. Create Your First Event-Sourced Application

See the [Complete Example: Account Ledger](#complete-example-account-ledger) section below for a full working example.

---

## Core Concepts (Learning Path)

### 🎓 Understanding the Building Blocks

Event Sourcing has 4 main components. Let's understand each one step by step:

#### 1️⃣ Events - "What Happened"

**Simple Explanation:**
Events are like entries in a diary - they record facts about what happened.

**Example:**
```
"On January 15th at 2:30 PM, John withdrew $100 from ATM on Main Street"
```

This becomes:
```java
MoneyWithdrawnEvent {
    amount: $100,
    who: "John",
    when: "2025-01-15T14:30:00Z",
    where: "ATM Main Street"
}
```

**Key Rules:**
- ✅ **Past tense** - "MoneyWithdrawn" not "WithdrawMoney" (it already happened!)
- ✅ **Immutable** - Once written, never changed (like history)
- ✅ **Complete** - Contains all information needed

#### 2️⃣ Aggregates - "Business Rules Enforcer"

**Simple Explanation:**
Aggregates are like a security guard - they check if an action is allowed before letting it happen.

**Example:**
```java
// Someone tries to withdraw $1000
account.withdraw($1000);

// Aggregate checks:
// ❌ Is account frozen? → Reject
// ❌ Is balance sufficient? → Reject
// ✅ All good? → Generate "MoneyWithdrawnEvent"
```

**Key Rules:**
- ✅ **No database table** - Lives in memory only!
- ✅ **Validates commands** - Enforces business rules
- ✅ **Generates events** - Records what happened
- ✅ **Rebuilds from events** - Replays history to get current state

**Why no table?**
```
Traditional: Account table stores current balance
Event Sourcing: Events table stores all transactions
              → Aggregate calculates balance from events
```

#### 3️⃣ Read Models - "Fast Query View"

**Simple Explanation:**
Read Models are like a summary page - they show current state for fast lookups.

**Example:**
```
Events table (source of truth):
  - AccountOpened: $1000
  - MoneyDeposited: $500
  - MoneyWithdrawn: $200

Read Model table (for fast queries):
  - Account ID: acc-123
  - Current Balance: $1300
  - Last Transaction: 2025-01-15
```

**Key Rules:**
- ✅ **Has database table** - Traditional table for queries
- ✅ **Denormalized** - Optimized for reading, not writing
- ✅ **Eventually consistent** - Updated by projections
- ✅ **Disposable** - Can be rebuilt from events

#### 4️⃣ Projections - "Event Listener"

**Simple Explanation:**
Projections listen to events and update read models - like a secretary updating a summary.

**Example:**
```
Event happens: MoneyDepositedEvent($500)
                      ↓
Projection listens: "Oh, money was deposited!"
                      ↓
Updates read model: balance = balance + $500
```

### 🗄️ The Golden Rule: What Gets a Database Table?

**This is the most important concept to understand!**

| Component | Has Table? | Why? | Analogy |
|-----------|------------|------|---------|
| **Events** | ✅ YES | Source of truth - permanent record | Bank statement (every transaction) |
| **Snapshots** | ✅ YES | Performance - cached state | Bookmark in a book |
| **Read Models** | ✅ YES | Fast queries - current state | Summary page |
| **Aggregates** | ❌ **NO** | Business logic - temporary | Calculator (does math, doesn't store) |

**Common Mistake:**
```java
// ❌ WRONG: Creating a table for the aggregate
@Table("account_ledger")  // ← This defeats event sourcing!
public class AccountLedger extends AggregateRoot {
    private BigDecimal balance;  // Stored in table? NO!
}

// ✅ CORRECT: Aggregate has NO table
public class AccountLedger extends AggregateRoot {
    private BigDecimal balance;  // Calculated from events!
    // This lives in memory only
    // Rebuilt from events each time
}

// ✅ CORRECT: Read model HAS a table
@Table("account_ledger_read_model")  // ← For fast queries
public class AccountLedgerReadModel {
    private BigDecimal balance;  // Stored in table for fast access
}
```

**Why this matters:**
```
If you create a table for your aggregate, you're doing traditional CRUD, not event sourcing!

Event Sourcing:
  Events table → Aggregate (in memory) → Read Model table

Traditional CRUD:
  Account table ← Direct updates
```

### Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    WRITE SIDE (Commands)                        │
│                                                                 │
│  Client Request                                                 │
│       ↓                                                         │
│  AccountLedgerService (orchestration)                           │
│       ↓                                                         │
│  AccountLedger Aggregate (business rules)                       │
│       ↓                                                         │
│  Events (MoneyDepositedEvent, etc.)                             │
│       ↓                                                         │
│  EventStore (PostgreSQL events table)                           │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ Events Published
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    READ SIDE (Queries)                          │
│                                                                 │
│  AccountLedgerProjectionService (event listener)                │
│       ↓                                                         │
│  AccountLedgerReadModel (denormalized view)                     │
│       ↓                                                         │
│  AccountLedgerRepository (queries)                              │
│       ↓                                                         │
│  Fast Query Results                                             │
└─────────────────────────────────────────────────────────────────┘
```

### Why Do We Need Each Component?

#### 1. **Domain Events** (e.g., `MoneyDepositedEvent`)
- **What**: Immutable records of things that happened
- **Why**: The source of truth - complete audit trail
- **Storage**: ✅ `events` table
- **Example**: `MoneyDepositedEvent{amount: 100, source: "ATM", depositedBy: "user-123"}`

#### 2. **Aggregate Root** (`AccountLedger`)
- **What**: Business logic that enforces rules and generates events
- **Why**: Ensures business rules are never violated (e.g., no withdrawals from frozen accounts)
- **Storage**: ❌ **NO TABLE** - Lives in memory only!
- **Example**: `account.withdraw(100)` → validates balance → generates `MoneyWithdrawnEvent`

#### 3. **Event Store** (PostgreSQL `events` table)
- **What**: Append-only log of all events
- **Why**: Permanent, immutable record of everything that happened
- **Storage**: ✅ `events` table
- **Example**: Stores events in order with metadata (timestamp, user, correlation ID)

#### 4. **Snapshots** (`AccountLedgerSnapshot`)
- **What**: Cached state at a specific version
- **Why**: Performance - avoid replaying millions of events
- **Storage**: ✅ `snapshots` table
- **Example**: Instead of replaying 1M events, load snapshot at version 999,000 + replay 1,000 events (100x faster!)

#### 5. **Service Layer** (`AccountLedgerService`)
- **What**: Orchestrates loading aggregates, executing commands, saving events
- **Why**: Handles infrastructure concerns (transactions, retries, logging)
- **Storage**: ❌ No table - Pure business logic
- **Example**: `service.deposit(accountId, 100)` → loads aggregate → executes → saves

#### 6. **Read Model** (`AccountLedgerReadModel`)
- **What**: Denormalized view optimized for queries
- **Why**: Fast queries without replaying events
- **Storage**: ✅ `account_ledger_read_model` table (traditional table!)
- **Example**: `SELECT * FROM account_ledger_read_model WHERE balance > 10000` (instant!)
- **Important**: This is a **traditional table** that gets updated by projections

#### 7. **Projection Service** (`AccountLedgerProjectionService`)
- **What**: Keeps read model in sync with events
- **Why**: Maintains eventual consistency between write and read sides
- **Storage**: ❌ No table - Event handler logic
- **Example**: Listens to `MoneyDepositedEvent` → updates balance in read model table

#### 8. **Repository** (`AccountLedgerRepository`)
- **What**: Data access layer for read model
- **Why**: Clean abstraction for querying the read model table
- **Storage**: ❌ No table - Queries the `account_ledger_read_model` table
- **Example**: `repository.findByCustomerId(customerId)` - fast, indexed queries

### The Flow

**WRITE (Command):**
```
Deposit $100 → Service → Aggregate validates → MoneyDepositedEvent → EventStore
```

**READ (Query):**
```
Get balance → Repository → ReadModel table → Return instantly (no event replay!)
```

**PROJECTION (Sync):**
```
MoneyDepositedEvent → ProjectionService → Update ReadModel balance
```

### Why This Separation?

**Traditional Approach:**
```sql
UPDATE accounts SET balance = balance + 100 WHERE id = 'acc-123';
SELECT balance FROM accounts WHERE id = 'acc-123';
-- ❌ Lost: Who deposited? When? Why? From where?
-- ❌ Can't answer: "What was the balance yesterday?"
```

**Event Sourcing with Read Models:**
```java
// WRITE: Complete audit trail
MoneyDepositedEvent{amount: 100, source: "ATM", depositedBy: "user-123", timestamp: "..."}

// READ: Fast queries from read model
SELECT balance FROM account_ledger_read_model WHERE id = 'acc-123';  -- Instant!

// TIME TRAVEL: Replay events to any point in time
getBalanceAt("2025-10-17T15:30:00Z")  -- What was the balance yesterday at 3:30 PM?
```

**Benefits:**
- ✅ **Complete Audit Trail**: Every transaction recorded forever
- ✅ **Time Travel**: Reconstruct state at any point in time
- ✅ **Fast Queries**: Read model optimized for queries
- ✅ **Scalability**: Scale reads and writes independently
- ✅ **Business Intelligence**: Analyze transaction patterns
- ✅ **Regulatory Compliance**: SOX, PCI-DSS, GDPR requirements met

👉 **[See the complete tutorial for detailed explanations and code](./docs/tutorial-account-ledger.md)**

---

---

## Complete Example: Account Ledger

### 📚 Learning Approach

We'll build a **complete account system** step by step. Each step builds on the previous one.

**What we're building:**
A bank account that can:
- ✅ Open new accounts with initial deposit
- ✅ Deposit money
- ✅ Withdraw money (with overdraft protection)
- ✅ Track complete transaction history
- ✅ Query current balance instantly
- ✅ Time travel to see balance at any point in time

**The Journey:**
```
Step 1: Define Events (What can happen?)
   ↓
Step 2: Create Aggregate (What are the rules?)
   ↓
Step 3: Build Service (How do we orchestrate?)
   ↓
Step 4: Add Read Model (How do we query fast?)
   ↓
Step 5: Create Projection (How do we stay in sync?)
   ↓
Step 6: Use It! (Put it all together)
```

---

### Step 1: Define Domain Events

**🎯 Goal:** Define what can happen to a bank account.

**💡 Think of events as:**
- Entries in a ledger
- Facts that cannot be changed
- Answers to "What happened?"

**📝 Events we need:**
1. **AccountOpened** - A new account was created
2. **MoneyDeposited** - Money was added to the account
3. **MoneyWithdrawn** - Money was taken from the account

**🔑 Key Principles:**
- Use **past tense** (AccountOpened, not OpenAccount)
- Include **all relevant data** (who, what, when, where, why)
- Extend **AbstractDomainEvent** (provides common fields)
- Use **@DomainEvent** annotation (for type identification)

```java
// Event 1: Account was opened
@DomainEvent("account.opened")  // ← Unique identifier for this event type
@SuperBuilder                    // ← Lombok: generates builder pattern
@Getter                          // ← Lombok: generates getters
@NoArgsConstructor              // ← Required for deserialization
@AllArgsConstructor             // ← Required for builder
public class AccountOpenedEvent extends AbstractDomainEvent {
    private String accountNumber;      // e.g., "ACC-2025-001"
    private String accountType;        // e.g., "CHECKING", "SAVINGS"
    private UUID customerId;           // Who owns this account
    private BigDecimal initialDeposit; // Starting balance
    private String currency;           // e.g., "USD", "EUR"
}

// Event 2: Money was deposited
@DomainEvent("money.deposited")
@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyDepositedEvent extends AbstractDomainEvent {
    private BigDecimal amount;         // How much was deposited
    private String source;             // "Wire Transfer", "Cash Deposit", "Check"
    private String reference;          // External reference number
    private String depositedBy;        // User ID who made the deposit
}

// Event 3: Money was withdrawn
@DomainEvent("money.withdrawn")
@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyWithdrawnEvent extends AbstractDomainEvent {
    private BigDecimal amount;         // How much was withdrawn
    private String destination;        // "ATM", "Wire Transfer", "Check"
    private String reference;          // External reference number
    private String withdrawnBy;        // User ID who made the withdrawal
}
```

**💡 What you get from AbstractDomainEvent:**
```java
// These fields are automatically included in every event:
- UUID aggregateId;        // Which account this event belongs to
- Instant eventTimestamp;  // When this event happened
- String userId;           // Who triggered this event
- String correlationId;    // For tracing across services
- Map<String, Object> metadata; // Additional context
```

**📊 How events are stored:**
```json
{
  "eventId": "evt-123",
  "eventType": "money.deposited",
  "aggregateId": "acc-456",
  "eventData": {
    "amount": 500.00,
    "source": "Wire Transfer",
    "reference": "REF-789",
    "depositedBy": "user-123"
  },
  "timestamp": "2025-01-15T14:30:00Z",
  "metadata": {
    "userId": "user-123",
    "correlationId": "corr-abc"
  }
}
```

---

### Step 2: Implement Aggregate Root

**🎯 Goal:** Create the "brain" that enforces business rules and generates events.

**💡 Think of the aggregate as:**
- A security guard checking if actions are allowed
- A state machine that transitions based on events
- A calculator that derives current state from history

**🔑 Key Responsibilities:**
1. **Validate commands** - "Can this action happen?"
2. **Generate events** - "Record what happened"
3. **Apply events** - "Update internal state"
4. **Protect invariants** - "Never allow invalid state"

**⚠️ Critical Rule:**
The aggregate has **NO database table**. It lives in memory and is rebuilt from events each time.

**🔄 The Flow:**
```
Command (deposit $100)
   ↓
Aggregate validates (is account open? is amount positive?)
   ↓
Generate event (MoneyDepositedEvent)
   ↓
Apply event (balance = balance + $100)
   ↓
Event saved to database
```

```java
@Getter
public class AccountLedger extends AggregateRoot {

    // ⚠️ IMPORTANT: These fields are NOT stored in a database table!
    // They are calculated from events each time the aggregate is loaded
    private String accountNumber;
    private String accountType;
    private UUID customerId;
    private BigDecimal balance;        // ← Calculated from all deposit/withdrawal events
    private String currency;
    private boolean frozen;
    private boolean closed;

    // Constructor 1: For LOADING existing accounts from event history
    // Used when: Fetching an existing account to perform operations
    public AccountLedger(UUID id) {
        super(id, "AccountLedger");
        this.balance = BigDecimal.ZERO;
        // After this, loadFromHistory() will be called to replay events
    }

    // Constructor 2: For CREATING new accounts
    // Used when: Opening a brand new account
    public AccountLedger(UUID id, String accountNumber, String accountType,
                        UUID customerId, BigDecimal initialDeposit, String currency) {
        super(id, "AccountLedger");

        // Step 1: Validate business rules (guard clauses)
        if (initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative");
        }

        // Step 2: Generate event (record what happened)
        applyChange(AccountOpenedEvent.builder()
                .aggregateId(id)
                .accountNumber(accountNumber)
                .accountType(accountType)
                .customerId(customerId)
                .initialDeposit(initialDeposit)
                .currency(currency)
                .build());
        // Note: applyChange() will call on(AccountOpenedEvent) to update state
    }

    // ═══════════════════════════════════════════════════════════════
    // COMMANDS: Public methods that validate and generate events
    // ═══════════════════════════════════════════════════════════════

    // Command: Deposit money into the account
    public void deposit(BigDecimal amount, String source, String reference, String depositedBy) {
        // Step 1: Validate business rules
        if (closed) {
            throw new AccountClosedException("Cannot deposit to closed account");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive");
        }

        // Step 2: Generate event (if validation passes)
        applyChange(MoneyDepositedEvent.builder()
                .aggregateId(getId())
                .amount(amount)
                .source(source)
                .reference(reference)
                .depositedBy(depositedBy)
                .build());
        // Note: applyChange() will call on(MoneyDepositedEvent) to update balance
    }

    // Command: Withdraw money from the account
    public void withdraw(BigDecimal amount, String destination, String reference, String withdrawnBy) {
        // Step 1: Validate business rules
        if (closed) {
            throw new AccountClosedException("Cannot withdraw from closed account");
        }
        if (frozen) {
            throw new AccountFrozenException("Cannot withdraw from frozen account");
        }
        if (balance.compareTo(amount) < 0) {
            // ← This is the overdraft protection!
            throw new InsufficientFundsException("Insufficient funds");
        }

        // Step 2: Generate event (if validation passes)
        applyChange(MoneyWithdrawnEvent.builder()
                .aggregateId(getId())
                .amount(amount)
                .destination(destination)
                .reference(reference)
                .withdrawnBy(withdrawnBy)
                .build());
        // Note: applyChange() will call on(MoneyWithdrawnEvent) to update balance
    }

    // ═══════════════════════════════════════════════════════════════
    // EVENT HANDLERS: Private methods that update state
    // These are called automatically when events are applied
    // ═══════════════════════════════════════════════════════════════

    // Event handler: What to do when AccountOpenedEvent happens
    private void on(AccountOpenedEvent event) {
        // Simply update the internal state - no validation needed
        // (validation already happened in the constructor)
        this.accountNumber = event.getAccountNumber();
        this.accountType = event.getAccountType();
        this.customerId = event.getCustomerId();
        this.balance = event.getInitialDeposit();  // ← Starting balance
        this.currency = event.getCurrency();
        this.frozen = false;
        this.closed = false;
    }

    // Event handler: What to do when MoneyDepositedEvent happens
    private void on(MoneyDepositedEvent event) {
        // Add the deposited amount to the balance
        this.balance = this.balance.add(event.getAmount());  // ← Balance increases
    }

    // Event handler: What to do when MoneyWithdrawnEvent happens
    private void on(MoneyWithdrawnEvent event) {
        // Subtract the withdrawn amount from the balance
        this.balance = this.balance.subtract(event.getAmount());  // ← Balance decreases
    }
}
```

**💡 Understanding the Flow:**

**Creating a new account:**
```
1. new AccountLedger(id, "ACC-001", "CHECKING", customerId, $1000, "USD")
2. Constructor validates: initialDeposit >= 0 ✅
3. Constructor calls: applyChange(AccountOpenedEvent)
4. applyChange() calls: on(AccountOpenedEvent)
5. on() sets: balance = $1000
6. Event is added to uncommitted events list
7. Service saves events to database
```

**Depositing money:**
```
1. account.deposit($500, "Wire", "REF-123", "user-456")
2. deposit() validates: not closed ✅, amount > 0 ✅
3. deposit() calls: applyChange(MoneyDepositedEvent)
4. applyChange() calls: on(MoneyDepositedEvent)
5. on() updates: balance = $1000 + $500 = $1500
6. Event is added to uncommitted events list
7. Service saves events to database
```

**Loading an existing account:**
```
1. new AccountLedger(id)  // Empty aggregate
2. loadFromHistory([AccountOpenedEvent, MoneyDepositedEvent, MoneyWithdrawnEvent])
3. For each event:
   - on(AccountOpenedEvent) → balance = $1000
   - on(MoneyDepositedEvent) → balance = $1500
   - on(MoneyWithdrawnEvent) → balance = $1300
4. Final state: balance = $1300 (calculated from events!)
```

**🎯 Key Takeaway:**
The aggregate's state is **always derived from events**. There's no separate table storing the balance. The balance is calculated by replaying all events!

---

### Step 3: Create Service Layer

**🎯 Goal:** Orchestrate the complete flow from loading aggregates to saving events.

**💡 Think of the service as:**
- The conductor of an orchestra - coordinates all the pieces
- The glue between your API and your domain logic
- The transaction boundary - ensures all-or-nothing persistence

**🔑 Key Responsibilities:**
1. **Load aggregates** from event store (with snapshot optimization)
2. **Execute commands** on aggregates (business logic)
3. **Save events** atomically (all or nothing)
4. **Publish events** to message brokers (for other services)
5. **Handle concurrency** conflicts (retry logic)

**✨ The Magic Annotation:**
`@EventSourcingTransactional` does all the heavy lifting:
- Saves uncommitted events to the database
- Publishes events to Kafka/RabbitMQ
- Handles optimistic locking conflicts
- Ensures atomic operations (all succeed or all fail)

```java
@Service
@RequiredArgsConstructor
public class AccountLedgerService {

    private final EventStore eventStore;
    private final SnapshotStore snapshotStore;

    // Open new account
    @EventSourcingTransactional
    public Mono<AccountLedger> openAccount(String accountNumber, String accountType,
                                          UUID customerId, BigDecimal initialDeposit,
                                          String currency) {
        UUID accountId = UUID.randomUUID();

        return Mono.fromCallable(() -> new AccountLedger(
                    accountId, accountNumber, accountType, customerId, initialDeposit, currency
                ))
                .flatMap(account -> eventStore.appendEvents(
                        accountId,
                        "AccountLedger",
                        account.getUncommittedEvents(),
                        0L
                    )
                    .doOnSuccess(stream -> account.markEventsAsCommitted())
                    .thenReturn(account)
                );
    }

    // Deposit money
    @EventSourcingTransactional(retryOnConcurrencyConflict = true, maxRetries = 3)
    public Mono<AccountLedger> deposit(UUID accountId, BigDecimal amount,
                                      String description, String reference, String userId) {
        return loadAccount(accountId)
                .doOnNext(account -> account.deposit(amount, description, reference, userId))
                .flatMap(this::saveAccount);
    }

    // Withdraw money
    @EventSourcingTransactional(retryOnConcurrencyConflict = true, maxRetries = 3)
    public Mono<AccountLedger> withdraw(UUID accountId, BigDecimal amount,
                                       String description, String reference, String userId) {
        return loadAccount(accountId)
                .doOnNext(account -> account.withdraw(amount, description, reference, userId))
                .flatMap(this::saveAccount);
    }

    // Load aggregate (with snapshot optimization)
    private Mono<AccountLedger> loadAccount(UUID accountId) {
        return snapshotStore.loadLatestSnapshot(accountId, "AccountLedger")
                .cast(AccountLedgerSnapshot.class)
                .flatMap(snapshot -> loadAccountFromSnapshot(accountId, snapshot))
                .switchIfEmpty(loadAccountFromEvents(accountId));
    }

    private Mono<AccountLedger> loadAccountFromSnapshot(UUID accountId, AccountLedgerSnapshot snapshot) {
        return eventStore.loadEventStream(accountId, "AccountLedger", snapshot.getVersion())
                .map(stream -> {
                    AccountLedger account = AccountLedger.fromSnapshot(snapshot);
                    account.loadFromHistory(stream.getEvents());
                    return account;
                });
    }

    private Mono<AccountLedger> loadAccountFromEvents(UUID accountId) {
        return eventStore.loadEventStream(accountId, "AccountLedger")
                .map(stream -> {
                    AccountLedger account = new AccountLedger(accountId);
                    account.loadFromHistory(stream.getEvents());
                    return account;
                });
    }

    private Mono<AccountLedger> saveAccount(AccountLedger account) {
        return eventStore.appendEvents(
                    account.getId(),
                    "AccountLedger",
                    account.getUncommittedEvents(),
                    account.getCurrentVersion() - account.getUncommittedEventCount()
                )
                .doOnSuccess(stream -> account.markEventsAsCommitted())
                .thenReturn(account);
    }
}
```

### Step 4: Create Read Model for Fast Queries

**🎯 Goal:** Create a fast, queryable view of the current state.

**💡 The Problem:**
```
User asks: "Show me all accounts with balance > $10,000"

Without Read Model:
1. Load ALL accounts from event store
2. Replay ALL events for EACH account
3. Calculate balance for EACH account
4. Filter accounts with balance > $10,000
⏱️ Time: Could take minutes for thousands of accounts!

With Read Model:
1. SELECT * FROM account_ledger_read_model WHERE balance > 10000
⏱️ Time: Milliseconds!
```

**🔑 Key Principles:**
- ✅ **Has a database table** (unlike aggregates!)
- ✅ **Denormalized** - Optimized for reading, not writing
- ✅ **Eventually consistent** - Updated by projections (slight delay is OK)
- ✅ **Disposable** - Can be deleted and rebuilt from events
- ✅ **Query-optimized** - Indexes, joins, whatever you need

**📊 Comparison:**

| Aspect | Aggregate | Read Model |
|--------|-----------|------------|
| **Has Table?** | ❌ No | ✅ Yes |
| **Purpose** | Business logic | Fast queries |
| **Consistency** | Strongly consistent | Eventually consistent |
| **Optimized for** | Writing | Reading |
| **Can be deleted?** | ❌ No (source of truth) | ✅ Yes (can rebuild) |

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("account_ledger_read_model")
public class AccountLedgerReadModel {
    @Id
    private UUID accountId;
    private String accountNumber;
    private String accountType;
    private UUID customerId;
    private BigDecimal balance;
    private String currency;
    private boolean frozen;
    private boolean closed;
    private Instant openedAt;
    private Instant lastTransactionAt;
}

@Repository
public interface AccountLedgerRepository extends R2dbcRepository<AccountLedgerReadModel, UUID> {
    Mono<AccountLedgerReadModel> findByAccountNumber(String accountNumber);
    Flux<AccountLedgerReadModel> findByCustomerId(UUID customerId);

    @Query("SELECT * FROM account_ledger_read_model WHERE balance > :minBalance")
    Flux<AccountLedgerReadModel> findByBalanceGreaterThan(BigDecimal minBalance);
}
```

### Step 5: Create Projection to Keep Read Model in Sync

**🎯 Goal:** Listen to events and update the read model automatically.

**💡 Think of projections as:**
- A secretary updating a summary document
- A mirror reflecting changes from the event store
- A background worker keeping views up-to-date

**🔄 The Flow:**
```
1. User deposits $500
2. Service saves MoneyDepositedEvent to events table
3. Event is published to message broker
4. Projection listens and receives the event
5. Projection updates read model: balance = balance + $500
6. Read model is now up-to-date!
```

**🔑 Key Principles:**
- ✅ **Event-driven** - Reacts to events, doesn't poll
- ✅ **Idempotent** - Can process same event multiple times safely
- ✅ **Eventually consistent** - Small delay between write and read is OK
- ✅ **Rebuildable** - Can delete read model and rebuild from all events

**⏱️ Eventual Consistency Explained:**
```
Time: 10:00:00.000 - User deposits $500
Time: 10:00:00.001 - Event saved to database
Time: 10:00:00.002 - Event published to Kafka
Time: 10:00:00.005 - Projection receives event
Time: 10:00:00.006 - Read model updated

Gap: 6 milliseconds of "eventual consistency"
For most applications, this is perfectly acceptable!
```

```java
@Service
public class AccountLedgerProjectionService extends ProjectionService<AccountLedgerReadModel> {

    private final AccountLedgerRepository repository;

    @Override
    protected Mono<Void> handleEvent(EventEnvelope envelope) {
        Event event = envelope.getEvent();

        if (event instanceof AccountOpenedEvent e) {
            AccountLedgerReadModel readModel = AccountLedgerReadModel.builder()
                .accountId(e.getAggregateId())
                .accountNumber(e.getAccountNumber())
                .accountType(e.getAccountType())
                .customerId(e.getCustomerId())
                .balance(e.getInitialDeposit())
                .currency(e.getCurrency())
                .frozen(false)
                .closed(false)
                .openedAt(e.getEventTimestamp())
                .lastTransactionAt(e.getEventTimestamp())
                .build();
            return repository.save(readModel).then();

        } else if (event instanceof MoneyDepositedEvent e) {
            return repository.findById(e.getAggregateId())
                .flatMap(readModel -> {
                    readModel.setBalance(readModel.getBalance().add(e.getAmount()));
                    readModel.setLastTransactionAt(e.getEventTimestamp());
                    return repository.save(readModel);
                })
                .then();

        } else if (event instanceof MoneyWithdrawnEvent e) {
            return repository.findById(e.getAggregateId())
                .flatMap(readModel -> {
                    readModel.setBalance(readModel.getBalance().subtract(e.getAmount()));
                    readModel.setLastTransactionAt(e.getEventTimestamp());
                    return repository.save(readModel);
                })
                .then();
        }

        return Mono.empty();
    }
}
```

### Step 6: Use It in Your Application

```java
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountLedgerService service;
    private final AccountLedgerRepository repository;

    // WRITE: Open account
    @PostMapping
    public Mono<AccountLedger> openAccount(@RequestBody OpenAccountRequest request) {
        return service.openAccount(
            request.getAccountNumber(),
            request.getAccountType(),
            request.getCustomerId(),
            request.getInitialDeposit(),
            request.getCurrency()
        );
    }

    // WRITE: Deposit money
    @PostMapping("/{accountId}/deposit")
    public Mono<AccountLedger> deposit(
            @PathVariable UUID accountId,
            @RequestBody DepositRequest request) {
        return service.deposit(
            accountId,
            request.getAmount(),
            request.getDescription(),
            request.getReference(),
            request.getUserId()
        );
    }

    // WRITE: Withdraw money
    @PostMapping("/{accountId}/withdraw")
    public Mono<AccountLedger> withdraw(
            @PathVariable UUID accountId,
            @RequestBody WithdrawRequest request) {
        return service.withdraw(
            accountId,
            request.getAmount(),
            request.getDescription(),
            request.getReference(),
            request.getUserId()
        );
    }

    // READ: Get account (uses read model - FAST!)
    @GetMapping("/{accountId}")
    public Mono<AccountLedgerReadModel> getAccount(@PathVariable UUID accountId) {
        return repository.findById(accountId);
    }

    // READ: Get customer accounts (uses read model - FAST!)
    @GetMapping("/customer/{customerId}")
    public Flux<AccountLedgerReadModel> getCustomerAccounts(@PathVariable UUID customerId) {
        return repository.findByCustomerId(customerId);
    }

    // READ: High balance accounts (uses read model - FAST!)
    @GetMapping("/high-balance")
    public Flux<AccountLedgerReadModel> getHighBalanceAccounts(
            @RequestParam(defaultValue = "10000") BigDecimal minBalance) {
        return repository.findByBalanceGreaterThan(minBalance);
    }

    // TIME TRAVEL: Get account state at specific point in time
    @GetMapping("/{accountId}/at/{timestamp}")
    public Mono<AccountLedger> getAccountAtTime(
            @PathVariable UUID accountId,
            @PathVariable Instant timestamp) {
        return service.getAccountAtTime(accountId, timestamp);
    }
}
```

### 🎉 What You Get

Congratulations! You now have a complete event-sourced system. Here's what you've built:

#### ✅ Complete Audit Trail
```sql
-- Every transaction is recorded forever
SELECT * FROM events WHERE aggregate_id = 'acc-123' ORDER BY aggregate_version;

Result:
1. AccountOpenedEvent - $1,000 initial deposit
2. MoneyDepositedEvent - $500 wire transfer
3. MoneyWithdrawnEvent - $200 ATM withdrawal
Current balance: $1,300 (calculated from events)
```

#### ✅ Business Rules Enforced
```java
// Try to overdraw
account.withdraw($10,000);  // Balance is only $1,300

Result: InsufficientFundsException ❌
The aggregate protects your business rules!
```

#### ✅ Fast Queries
```java
// Find all high-balance accounts
repository.findByBalanceGreaterThan($10,000);

Result: Milliseconds! (uses indexed read model table)
```

#### ✅ Time Travel
```java
// What was the balance on January 15th?
service.getAccountAtTime(accountId, "2025-01-15T23:59:59Z");

Result: Replays events up to that timestamp
This is impossible with traditional databases!
```

#### ✅ Automatic Synchronization
```
Event happens → Projection listens → Read model updates
All automatic, no manual sync code needed!
```

#### ✅ Production-Ready Features
- **ACID Transactions** - @EventSourcingTransactional ensures atomicity
- **Concurrency Control** - Optimistic locking prevents conflicts
- **Performance** - Snapshots optimize loading
- **Scalability** - Reactive, non-blocking operations
- **Observability** - Distributed tracing built-in

### 🧠 Putting It All Together

**The Complete Flow (Deposit $500):**

```
1. User clicks "Deposit $500" in UI
   ↓
2. POST /accounts/{id}/deposit
   ↓
3. AccountLedgerService.deposit()
   ↓
4. Load account from events (or snapshot + recent events)
   ↓
5. AccountLedger.deposit($500)
   - Validates: not closed ✅, amount > 0 ✅
   - Generates: MoneyDepositedEvent
   - Updates state: balance = balance + $500
   ↓
6. @EventSourcingTransactional saves event to database
   ↓
7. Event published to Kafka
   ↓
8. AccountLedgerProjectionService receives event
   ↓
9. Updates read model: balance = balance + $500
   ↓
10. User queries GET /accounts/{id}
    → Returns updated balance instantly from read model!
```

**Database State After Deposit:**

```sql
-- events table (source of truth)
INSERT INTO events (event_type, event_data, ...)
VALUES ('money.deposited', '{"amount": 500, ...}', ...);

-- account_ledger_read_model table (for fast queries)
UPDATE account_ledger_read_model
SET balance = balance + 500
WHERE account_id = 'acc-123';
```

**What's NOT in the database:**
```
❌ No "account_ledger" table for the aggregate
❌ Aggregate lives in memory only
❌ State is always calculated from events
```

---

## Architecture

### Event Sourcing Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    WRITE SIDE (Commands)                        │
│                                                                 │
│  Client Request (POST /deposit)                                 │
│       ↓                                                         │
│  AccountLedgerService (orchestration)                           │
│       ↓                                                         │
│  AccountLedger Aggregate (business rules)                       │
│       ↓                                                         │
│  Events (MoneyDepositedEvent, etc.)                             │
│       ↓                                                         │
│  EventStore → PostgreSQL events table                           │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ Events Published
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    READ SIDE (Queries)                          │
│                                                                 │
│  AccountLedgerProjectionService (event listener)                │
│       ↓                                                         │
│  AccountLedgerReadModel (denormalized view)                     │
│       ↓                                                         │
│  PostgreSQL account_ledger_read_model table                     │
│       ↓                                                         │
│  AccountLedgerRepository (queries)                              │
│       ↓                                                         │
│  Fast Query Results (GET /accounts/{id})                        │
└─────────────────────────────────────────────────────────────────┘
```

### Component Responsibilities

| Component | Responsibility | Has Table? |
|-----------|---------------|------------|
| **Domain Events** | Immutable facts about what happened | ✅ `events` |
| **Aggregate Root** | Business logic + state reconstruction | ❌ In-memory |
| **Event Store** | Persist and retrieve events | ✅ `events` |
| **Snapshots** | Performance optimization | ✅ `snapshots` |
| **Service Layer** | Orchestrate operations | ❌ Logic only |
| **Read Model** | Denormalized query view | ✅ Custom table |
| **Projection** | Keep read model in sync | ❌ Logic only |
| **Repository** | Query read model | ❌ Data access |

---

## Configuration

### Database Configuration

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/eventstore
    username: postgres
    password: postgres
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### Event Sourcing Configuration

```yaml
firefly:
  eventsourcing:
    enabled: true

    # Event Store
    store:
      type: r2dbc
      batch-size: 100
      connection-timeout: 30s
      query-timeout: 30s
      max-events-per-load: 1000

    # Snapshots
    snapshot:
      enabled: true
      threshold: 50              # Create snapshot every 50 events
      keep-count: 3              # Keep last 3 snapshots
      compression: true          # Compress snapshot data
      caching: true              # Cache snapshots in memory

    # Event Publishing
    publisher:
      enabled: true
      type: KAFKA
      destination-prefix: events
      async: true
      batch-size: 10

    # Optional: Circuit Breaker
    resilience:
      circuit-breaker:
        enabled: true
        failure-rate-threshold: 50
        wait-duration-in-open-state: 60s

    # Optional: Distributed Tracing
    tracing:
      enabled: true

    # Optional: Multi-tenancy
    multitenancy:
      enabled: false
```

---

## Testing

### Unit Testing Aggregates

```java
@Test
void shouldDepositMoney() {
    // Given
    UUID accountId = UUID.randomUUID();
    AccountLedger account = new AccountLedger(
        accountId, "ACC-001", "CHECKING",
        customerId, BigDecimal.valueOf(1000), "USD"
    );

    // When
    account.deposit(
        BigDecimal.valueOf(500),
        "Wire Transfer",
        "REF-123",
        "user-456"
    );

    // Then
    assertEquals(BigDecimal.valueOf(1500), account.getBalance());
    assertEquals(2, account.getUncommittedEventCount());
}

@Test
void shouldPreventOverdraft() {
    // Given
    AccountLedger account = new AccountLedger(accountId);
    account.loadFromHistory(List.of(
        new AccountOpenedEvent(/* ... */, BigDecimal.valueOf(100))
    ));

    // When/Then
    assertThrows(InsufficientFundsException.class, () ->
        account.withdraw(BigDecimal.valueOf(200), "ATM", "REF-456", "user-123")
    );
}
```

### Integration Testing with Testcontainers

```java
@SpringBootTest
@Testcontainers
class AccountLedgerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("eventstore_test");

    @Autowired
    private AccountLedgerService service;

    @Autowired
    private AccountLedgerRepository repository;

    @Test
    void shouldPersistEventsAndUpdateReadModel() {
        // Given
        UUID customerId = UUID.randomUUID();

        // When: Open account
        AccountLedger account = service.openAccount(
            "ACC-001", "CHECKING", customerId,
            BigDecimal.valueOf(1000), "USD"
        ).block();

        // Then: Read model should be updated
        AccountLedgerReadModel readModel = repository
            .findById(account.getId())
            .block();

        assertNotNull(readModel);
        assertEquals(BigDecimal.valueOf(1000), readModel.getBalance());
        assertEquals("ACC-001", readModel.getAccountNumber());
    }
}
```

---

## Documentation

### 📚 Complete Guides

- **[Complete Tutorial: Account Ledger](./docs/tutorial-account-ledger.md)** - Step-by-step guide building a complete system
- **[Event Sourcing Explained](./docs/event-sourcing-explained.md)** - Deep dive into concepts and patterns
- **[Architecture Overview](./docs/architecture.md)** - System design and component interactions
- **[Configuration Reference](./docs/configuration.md)** - All configuration options explained
- **[API Reference](./docs/api-reference.md)** - Detailed interface documentation
- **[Testing Guide](./docs/testing.md)** - Testing strategies with Testcontainers
- **[Production Readiness](./PRODUCTION-READINESS.md)** - Deployment checklist and best practices
- **[Optional Enhancements](./docs/optional-enhancements.md)** - Circuit breakers, tracing, multi-tenancy

### 💡 Examples

- **[Banking Example](./docs/examples/banking-example.md)** - Complete example system
- **[Improved Developer Experience](./docs/examples/improved-developer-experience.md)** - Using AbstractDomainEvent

---

## Best Practices

### Event Design
- ✅ Use **past tense names** (`AccountOpened`, not `OpenAccount`)
- ✅ Make events **immutable** (final fields, no setters)
- ✅ Include **all necessary data** (avoid lookups when replaying)
- ✅ Keep events **small and focused** (single responsibility)
- ✅ Use **@DomainEvent** annotation for type identification

### Aggregate Design
- ✅ Keep aggregates **small** (single consistency boundary)
- ✅ **Validate in commands**, **apply in event handlers**
- ✅ Avoid loading **multiple aggregates** in one transaction
- ✅ Use **eventual consistency** between aggregates
- ✅ **No database table** for aggregates (in-memory only)

### Performance
- ✅ Enable **snapshots** for aggregates with many events
- ✅ Configure appropriate **batch sizes** (100-1000 events)
- ✅ Use **read models** for complex queries
- ✅ Monitor **event store performance** (query times, storage)
- ✅ Consider **archiving** old events (after snapshots)

### Error Handling
- ✅ Handle **concurrency conflicts** with retries
- ✅ Use **circuit breakers** for external dependencies
- ✅ Monitor **failed event publishing** (dead letter queue)
- ✅ Implement **idempotent** event handlers
- ✅ Log **correlation IDs** for distributed tracing

### Security
- ✅ Store **user context** in event metadata
- ✅ Implement **authorization** in command handlers
- ✅ **Encrypt sensitive data** in events (PII, PCI)
- ✅ Use **multi-tenancy** for SaaS applications
- ✅ Audit **who, what, when** for compliance

---

## Integration with Other Firefly Libraries

- **[fireflyframework-r2dbc](https://github.org/fireflyframework-oss/fireflyframework-r2dbc)** - Reactive database access and transaction management
- **[fireflyframework-eda](https://github.org/fireflyframework-oss/fireflyframework-eda)** - Event publishing to Kafka and other message brokers
- **[lib-transactional-engine](https://github.org/fireflyframework-oss/lib-transactional-engine)** - Saga orchestration for distributed transactions
- **[fireflyframework-cache](https://github.org/fireflyframework-oss/fireflyframework-cache)** - Snapshot caching and performance optimization

---

## Contributing

Contributions are welcome! Please read our [Contributing Guidelines](CONTRIBUTING.md) before submitting pull requests.

---

## License

Copyright 2024-2026 Firefly Software Solutions Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

---

## Support

- 📧 **Email**: support@getfirefly.io
- 💬 **Discussions**: [GitHub Discussions](https://github.org/fireflyframework-oss/fireflyframework-eventsourcing/discussions)
- 🐛 **Issues**: [GitHub Issues](https://github.org/fireflyframework-oss/fireflyframework-eventsourcing/issues)
- 📖 **Documentation**: [Full Documentation](./docs/)

---

**Built with ❤️ by the Firefly Team**