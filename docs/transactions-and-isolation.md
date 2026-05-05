# Transactions & Isolation – Correctness in Concurrent Systems

Transactions are the backbone of **data correctness** in concurrent systems.

This phase explains:
- What transactions actually guarantee
- How isolation levels work
- Why concurrency causes anomalies
- How to reason about trade‑offs in real systems

Strong backend engineers understand **when strict isolation is required and when it is not**.

---

## What Is a Transaction?

> **A transaction is a sequence of database operations executed as a single logical unit of work.**

A transaction ensures that:
✅ Either all changes happen  
✅ Or none of them do  

There is no partial state.

---

## ACID Properties (High-Level, Interview Essential)

Every transaction follows **ACID**:

### ✅ Atomicity
All operations succeed or all fail.

### ✅ Consistency
Transaction moves database from one valid state to another.

### ✅ Isolation
Concurrent transactions don’t interfere improperly.

### ✅ Durability
Once committed, data survives crashes.

Interview insight:
> **ACID describes guarantees, not implementation.**

---

## Why Transactions Are Needed

Without transactions:
❌ Partial updates  
❌ Data corruption  
❌ Race conditions  

Example:
- Money deducted from one account
- Credit fails in another account  
→ inconsistent state

Transactions prevent this.

---

## Concurrency Problems (Interview Favorites)

When multiple transactions run concurrently, anomalies can occur.

---

### 1️⃣ Dirty Read

> Reading data written by an uncommitted transaction.

Problem:
- Data may be rolled back
- Reader sees invalid data

---

### 2️⃣ Non-Repeatable Read

> Same query returns different results within the same transaction.

Cause:
- Another transaction modifies data between reads

---

### 3️⃣ Phantom Read

> New rows appear when the same query is executed again.

Cause:
- Another transaction inserts matching rows

---

### 4️⃣ Lost Update

> Two transactions overwrite each other’s updates.

Very serious in financial systems.

---

## Isolation Levels (Core Interview Topic)

> **Isolation level defines how visible changes from one transaction are to others.**

Higher isolation = stronger correctness, lower concurrency.

---

## The Four Standard Isolation Levels

### ✅ Read Uncommitted
- Dirty reads allowed
- Rarely used

---

### ✅ Read Committed
- Only committed data visible
- Prevents dirty reads
- Non-repeatable reads possible

Used by many databases by default.

---

### ✅ Repeatable Read
- Rows read cannot change
- Prevents dirty & non-repeatable reads
- Phantom reads still possible (in theory)

Common in production systems.

---

### ✅ Serializable
- Highest isolation
- Transactions behave as if executed one by one
- Strong correctness, low concurrency

Used when correctness is critical.

---

## Isolation Levels Summary Table (Conceptual)

| Level | Dirty Read | Non-repeatable | Phantom |
|-----|-----------|----------------|---------|
| Read Uncommitted | ✅ | ✅ | ✅ |
| Read Committed | ❌ | ✅ | ✅ |
| Repeatable Read | ❌ | ❌ | ✅ |
| Serializable | ❌ | ❌ | ❌ |

You don't need to memorize — understand the progression.

---

## Choosing the Right Isolation Level

Interview insight:
> **Not all systems need Serializable isolation.**

Choose based on domain:
- Payments → Strong isolation
- Reporting → Lower isolation
- Feeds → Eventual consistency

Isolation is a **business decision**.

---

## Transactions vs Performance

Higher isolation:
✅ Strong correctness  
❌ Reduced throughput  
❌ More locking  

Lower isolation:
✅ Better performance  
❌ Weaker guarantees  

Balance matters more than purity.

---

## Locks (High Level)

Databases use locks to:
✅ Protect data  
✅ Enforce isolation  

Types (conceptual):
- Read locks
- Write locks

Poor locking strategies cause:
❌ Deadlocks  
❌ Slow queries  

Design schemas to minimize lock contention.

---

## Deadlocks

> **Deadlock occurs when two transactions wait on each other indefinitely.**

Databases handle deadlocks by:
✅ Detecting cycles  
✅ Rolling back one transaction  

Deadlocks are expected — systems must handle them.

---

## Transactions in Distributed Systems

Important insight:
> **ACID guarantees weaken as systems distribute.**

Challenges:
- Network failures
- Partial failure
- Coordination cost

This is why:
✅ Distributed transactions are avoided
✅ Eventual consistency is common

---

## Practical Transaction Design Principles

✅ Keep transactions short  
✅ Avoid user interaction inside transactions  
✅ Touch minimal rows  
✅ Choose isolation intentionally  

Transactions are **expensive resources**.

---

## Interview‑Ready Explanation (Use This)

> “Transactions guarantee atomicity and consistency in concurrent systems. Isolation levels control how transactions interact, with higher isolation providing stronger correctness at the cost of performance. The correct isolation level depends on business requirements, not theoretical purity.”

Clear. Calm. Senior‑level ✅

---

## Key Takeaways

✅ Transactions preserve correctness  
✅ Isolation controls concurrency behavior  
✅ Higher isolation means lower performance  
✅ Business needs determine guarantees  

> **Concurrency correctness is a design choice, not a default setting.**
