# Database Design – Overview & Mindset

Database design is one of the **most important and most underestimated skills**
of a backend engineer.

A well‑designed database:
✅ Supports scalability  
✅ Improves performance  
✅ Simplifies application logic  

A poorly designed database:
❌ Becomes a bottleneck  
❌ Limits growth  
❌ Causes system instability  

---

## What Is Database Design?

> **Database design is the process of structuring data so that it can be stored, queried, and modified efficiently while maintaining correctness and scalability.**

It answers questions like:
- How should data be represented?
- How do entities relate?
- How will data be accessed?
- How will it scale?

---

## Common Beginner Mistake (Interview Red Flag)

❌ Designing tables only based on current API needs

Correct mindset:
✅ Design for **data behavior**
✅ Design for **access patterns**
✅ Design for **future growth**

---

## First Rule of Database Design

> **Design based on queries, not just entities.**

Always ask:
- What queries will run most often?
- What reads are latency‑sensitive?
- What writes are frequent?
- What joins are expensive?

---

## OLTP vs OLAP (High Level)

### OLTP (Transactional Systems)
- Many small reads/writes
- User‑facing applications
- Strong consistency

Examples:
✅ E‑commerce  
✅ Payments  
✅ User profiles  

---

### OLAP (Analytical Systems)
- Fewer but large queries
- Aggregations
- Reporting

Examples:
✅ Analytics  
✅ Metrics  
✅ Business intelligence  

Most backend systems start as OLTP.

---

## Core Database Design Goals

✅ Correctness  
✅ Performance  
✅ Maintainability  
✅ Scalability  

Trade‑offs are inevitable — priorities must be explicit.

---

## Key Design Inputs

A good database design considers:

✅ Data volume  
✅ Read/write ratio  
✅ Query patterns  
✅ Consistency requirements  
✅ Indexing strategy  

These inputs matter more than technology choice.

---

## Relational Databases (Why They Still Matter)

Relational databases excel at:
✅ Structured data  
✅ Transactions  
✅ Relationships  
✅ Strong consistency  

They remain the **default choice** for most business systems.

---

## When Databases Become Bottlenecks

Common causes:
❌ Poor schema design  
❌ Missing or wrong indexes  
❌ Over‑normalized schemas  
❌ Uncontrolled data growth  

Most performance issues are **design issues**, not hardware issues.

---

## Interview‑Ready Insight

> **Databases scale with good design, not bigger machines.**

This line alone signals maturity.

---

## Key Takeaways

✅ Database design is architectural, not tactical  
✅ Queries drive schema design  
✅ Performance issues often start at the data layer  
✅ Early design decisions last for years  
