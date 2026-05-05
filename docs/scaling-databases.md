
# Scaling Databases – Growing Beyond a Single Machine

Databases do not remain small forever.

As systems grow:
- Data volume increases
- Traffic grows
- Latency requirements tighten
- Failure impact becomes larger

This phase explains **how databases scale**, **when each strategy is used**, and **what trade‑offs are involved**.

---

## What Does Database Scaling Mean?

> **Database scaling is the process of handling increased data volume and request load without sacrificing performance or availability.**

Scaling can target:
✅ Read throughput  
✅ Write throughput  
✅ Storage capacity  
✅ Availability  

---

## The First Scaling Rule (Interview Critical)

> **You cannot scale a poorly designed database.**

Bad schema + bad queries = scaling failure  
Good schema + good access patterns = smooth scaling  

Scaling is a **design problem**, not an infrastructure problem.

---

## Vertical vs Horizontal Scaling

### Vertical Scaling (Scale Up)

Increase:
- CPU
- RAM
- Disk

✅ Simple  
❌ Hard limit  
❌ Expensive  
❌ Single point of failure  

Used as:
✅ Short‑term solution

---

### Horizontal Scaling (Scale Out)

Add:
- More machines

✅ Scales indefinitely  
✅ Fault tolerant  
✅ Cost efficient  

Most production systems move here eventually.

---

## Scaling Reads – Read Replicas

> **Read replicas handle read traffic while the primary handles writes.**

Architecture:

```

App
├── Primary DB (Writes)
└── Read Replicas (Reads)

```

---

### Benefits

✅ Increased read throughput  
✅ Reduced load on primary  
✅ Simple to implement  

---

### Trade‑Offs

❌ Replication lag  
❌ Eventual consistency  

Interview insight:
> **Read replicas trade freshness for scalability.**

---

## Write Scaling – Harder Problem

Writes are inherently harder to scale than reads.

Why?
- Strong consistency requirements
- Ordering constraints
- Conflict risk

This leads to **sharding**.

---

## Sharding (Horizontal Partitioning)

> **Sharding splits data across multiple databases based on a shard key.**

Each shard:
✅ Owns a subset of data  
✅ Handles reads and writes independently  

---

### Common Shard Keys

✅ user_id  
✅ tenant_id  
✅ region  

Shard keys must:
- Be evenly distributed
- Be immutable
- Avoid hotspots

---

## Sharding Strategies

### 1️⃣ Range‑Based Sharding

Example:
- Users 1–1000 → Shard A
- Users 1001–2000 → Shard B

✅ Simple  
❌ Hotspot risk  

---

### 2️⃣ Hash‑Based Sharding (Most Common)

- Hash(key) → shard

✅ Even distribution  
❌ Harder range queries  

Preferred for large systems.

---

### 3️⃣ Directory‑Based Sharding

- Lookup service maps key → shard

✅ Flexible  
❌ Extra hop  

Used when re‑sharding is frequent.

---

## Challenges with Sharding

❌ Cross‑shard joins  
❌ Cross‑shard transactions  
❌ Data rebalancing  
❌ Application complexity  

Interview insight:
> **Sharding moves complexity from database to application.**

---

## Re‑Sharding (Reality Check)

> **Your first shard key will not be perfect.**

Design for:
✅ Future rebalancing  
✅ Minimal downtime  
✅ Background migration  

Never assume shard key is final.

---

## Caching as a Scaling Layer

Caching reduces database load drastically.

Use cases:
✅ Hot reads  
✅ Aggregated data  
✅ Derived data  

Cache sits between:
```

App → Cache → Database

```

Cache is **not a scaling substitute**, but a **scaling amplifier**.

---

## Multi‑Region Database Scaling (High Level)

Global systems need:
✅ Regional reads  
✅ Disaster recovery  
✅ Low latency across geographies  

Trade‑off:
- Latency vs consistency
- Complexity increases rapidly

Only large systems need this.

---

## Scaling Transactions

Higher scale means:
❌ Distributed transactions become expensive  

Result:
✅ Prefer eventual consistency  
✅ Use sagas instead of 2PC  

Transactions become **local**, not global.

---

## Practical Scaling Strategy (Real‑World)

1️⃣ Optimize schema and queries  
2️⃣ Add proper indexes  
3️⃣ Introduce caching  
4️⃣ Add read replicas  
5️⃣ Shard when absolutely needed  

Never start at step 5.

---

## Interview‑Ready Explanation (Use This)

> “Databases scale by first optimizing schema and queries, then adding read replicas for read scalability. Write scaling is achieved through sharding, which distributes data across multiple nodes. These choices introduce trade‑offs between consistency, availability, and complexity.”

Clear. Calm. Senior‑level ✅

---

## Common Interview Mistakes (Red Flags)

❌ “Sharding solves everything”  
❌ “Scale vertically forever”  
❌ Ignoring replication lag  
❌ Cross‑shard joins everywhere  

---

## Key Takeaways

✅ Scaling starts with design  
✅ Reads scale easier than writes  
✅ Read replicas help read traffic  
✅ Sharding enables write scaling  
✅ Complexity increases with scale  

> **Scaling databases is about trade‑offs, not magic solutions.**
