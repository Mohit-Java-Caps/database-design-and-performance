
# Indexing Fundamentals – Making Queries Fast (and Knowing the Cost)

Indexes are one of the **most powerful tools** in database performance engineering.

A well‑designed index can:
✅ Turn a slow query into a fast one  

A poorly designed index can:
❌ Waste memory  
❌ Slow down writes  
❌ Confuse query planners  

Strong backend engineers understand **when and why to use indexes**, not just how.

---

## What Is an Index?

> **An index is a data structure that allows the database to find rows quickly without scanning the entire table.**

Think of an index like:
> *The index of a book* — you don’t read every page to find a topic.

---

## Why Indexes Exist

Without indexes:
❌ Database performs full table scans  
❌ Query time grows with data size  

With indexes:
✅ Queries scale logarithmically  
✅ Performance remains predictable  

Indexing is essential for scalability.

---

## How Databases Find Data (High Level)

When you run a query:
```sql
SELECT * FROM users WHERE email = 'a@b.com';
````

The database can either:

*   Scan every row (full table scan), or
*   Use an index to jump directly to matching rows

Indexes exist to avoid **full table scans**.

***

## Common Index Data Structures

### B‑Tree Index (Most Common)

> **B‑Tree indexes keep data sorted and allow efficient range queries.**

Characteristics:
✅ Balanced tree  
✅ Logarithmic lookup time  
✅ Efficient for equality and range queries

Used by:

*   Most relational databases by default

***

### Hash Index (Conceptual)

> **Hash indexes work well for equality lookups only.**

Characteristics:
✅ Fast exact match  
❌ No range queries

Less commonly used in relational databases.

***

## When Indexes Are Used

Indexes help when:
✅ Columns appear in `WHERE`, `JOIN`, or `ORDER BY` clauses  
✅ Queries filter on indexed columns  
✅ Result set is small relative to table size

Indexes do **not** help every query.

***

## When Indexes Do NOT Help

❌ Very small tables  
❌ Low‑selectivity columns  
❌ Queries that return most rows  
❌ Heavy write‑only workloads

Example of low selectivity:

    status = 'ACTIVE'  // if most rows are ACTIVE

***

## Index Selectivity (Interview Favorite)

> **Selectivity measures how unique column values are.**

High selectivity:
✅ email  
✅ user\_id

Low selectivity:
❌ boolean flags  
❌ gender  
❌ status (often)

Indexes are most effective on **high‑selectivity columns**.

***

## Single‑Column Index

Basic form:

```sql
CREATE INDEX idx_user_email ON users(email);
```

Use when:
✅ Queries filter primarily on one column  
✅ Simple access patterns

***

## Composite Index (Very Important)

> **A composite index indexes multiple columns together.**

Example:

```sql
CREATE INDEX idx_order_user_date ON orders(user_id, created_at);
```

Key concept:

> **Index order matters.**

This index helps:
✅ `WHERE user_id = ?`  
✅ `WHERE user_id = ? AND created_at > ?`

But NOT:
❌ `WHERE created_at = ?` alone

***

## Left‑Most Prefix Rule (Must Know)

A composite index on:

    (user_id, created_at, status)

Can be used for:
✅ user\_id  
✅ user\_id + created\_at  
✅ user\_id + created\_at + status

❌ created\_at alone

This rule is heavily tested in interviews.

***

## Indexes and Sorting

Indexes can also optimize sorting.

Example:

```sql
SELECT * FROM orders
WHERE user_id = ?
ORDER BY created_at DESC;
```

✅ Works efficiently if index matches filter + order

Otherwise:
❌ Requires expensive sorting

***

## Indexes and Joins

Indexes are critical for join performance.

Rule:

> **Foreign keys should almost always be indexed.**

Example:

    Order.user_id → User.user_id

Without index:
❌ Nested loop joins become slow

***

## Write Cost of Indexes (Trade‑Off)

Every index:
✅ Improves reads  
❌ Slows down writes

Why?

*   Insert/update must update every index

Interview insight:

> **Indexes optimize reads at the cost of writes.**

***

## Over‑Indexing (Common Mistake)

❌ Creating indexes for every column  
❌ Indexing without understanding queries

Symptoms:

*   Slow inserts
*   High memory usage
*   Complex maintenance

Indexes must be **intentional**.

***

## Covering Indexes (Advanced But Useful)

> **A covering index contains all columns needed by a query.**

Example:

```sql
SELECT user_id, created_at
FROM orders
WHERE user_id = ?;
```

If index contains both:
✅ No table lookup needed

This improves performance further.

***

## How to Decide What to Index

Always ask:

*   What are the most frequent queries?
*   Which filters are used most?
*   Which joins are common?
*   What is the selectivity?

> **Index for queries, not for columns.**

***

## Interview‑Ready Explanation (Use This)

> “Indexes speed up queries by avoiding full table scans, usually using B‑Tree structures. They are most effective on high‑selectivity columns and queries in WHERE, JOIN, and ORDER BY clauses. Indexes improve read performance but add overhead to writes, so they must be designed based on access patterns.”

Clear. Confident. Senior‑level ✅

***

## Key Takeaways

✅ Indexes are access‑pattern driven  
✅ B‑Tree indexes are the default  
✅ Composite indexes require careful column order  
✅ Indexes speed up reads but slow down writes  
✅ Over‑indexing hurts performance

> **Good indexing is about balance, not abundance.**
