
# 🗄️ Database Interview Questions & Answers (Backend Focus)

***

## 1️⃣ What is database design?

**Answer (spoken style):**

> Database design is the process of structuring data so that it can be stored, queried, and updated efficiently while maintaining correctness and scalability.  
> It focuses on entities, relationships, access patterns, and long‑term growth, not just table creation.

***

## 2️⃣ What is data modeling and why is it important?

**Answer:**

> Data modeling defines entities, attributes, relationships, and cardinality before creating tables.  
> It is important because a good model simplifies queries and scales naturally, while a bad model causes performance problems and frequent schema changes.

***

## 3️⃣ What is normalization?

**Answer:**

> Normalization is the process of reducing data redundancy and improving data integrity by storing data in logically separate tables and using relationships.  
> It prevents update anomalies and keeps data consistent.

***

## 4️⃣ When should you denormalize a database?

**Answer:**

> Denormalization is used when read performance is more important than strict normalization.  
> It is common in read‑heavy systems like feeds, reporting, and search, where reducing joins improves latency.  
> In practice, most systems use a hybrid approach.

***

## 5️⃣ What are the drawbacks of denormalization?

**Answer:**

> Denormalization introduces data duplication, which increases the risk of inconsistency.  
> It also makes updates more complex and often requires asynchronous or event‑based synchronization to keep data consistent.

***

## 6️⃣ What is an index?

**Answer:**

> An index is a data structure that allows the database to locate rows quickly without scanning the entire table.  
> Indexes improve read performance but increase write overhead.

***

## 7️⃣ How do indexes improve performance?

**Answer:**

> Indexes allow the database to find matching rows using logarithmic lookup instead of full table scans.  
> This keeps query performance stable even as data grows.

***

## 8️⃣ What is index selectivity?

**Answer:**

> Selectivity measures how unique a column’s values are.  
> High‑selectivity columns like `email` or `user_id` benefit most from indexing, while low‑selectivity columns like boolean flags usually do not.

***

## 9️⃣ What is a composite index and why does order matter?

**Answer:**

> A composite index is an index on multiple columns.  
> The column order matters because databases follow the left‑most prefix rule — queries must filter starting from the first column of the index to use it efficiently.

***

## 🔟 Why do indexes slow down writes?

**Answer:**

> Every insert, update, or delete must also update all related indexes.  
> So while indexes speed up reads, they increase write cost and storage usage.

***

## 1️⃣1️⃣ What is a transaction?

**Answer:**

> A transaction is a sequence of database operations executed as a single logical unit.  
> It ensures that either all operations succeed or none of them are applied.

***

## 1️⃣2️⃣ What does ACID mean?

**Answer:**

> ACID stands for Atomicity, Consistency, Isolation, and Durability.  
> These properties guarantee correctness in concurrent and failure‑prone environments.  
> ACID describes guarantees, not how the database is implemented.

***

## 1️⃣3️⃣ What are isolation levels?

**Answer:**

> Isolation levels define how much a transaction is isolated from others.  
> Higher isolation prevents anomalies but reduces concurrency, while lower isolation improves performance with weaker guarantees.

***

## 1️⃣4️⃣ What are common concurrency problems?

**Answer:**

> Common problems include dirty reads, non‑repeatable reads, phantom reads, and lost updates.  
> These occur when multiple transactions modify or read data concurrently without proper isolation.

***

## 1️⃣5️⃣ How do you choose the right isolation level?

**Answer:**

> The isolation level should be chosen based on business requirements.  
> Payments require strong isolation, while feeds and analytics can tolerate eventual consistency for better performance.

***

## 1️⃣6️⃣ Why are queries slow even with indexes?

**Answer:**

> Queries can still be slow if indexes are missing, poorly ordered, low‑selectivity, or if the query scans too many rows, joins large datasets, or fetches unnecessary columns.

***

## 1️⃣7️⃣ What is the N+1 query problem?

**Answer:**

> The N+1 problem occurs when one query fetches parent records and N additional queries fetch child records individually.  
> It causes unnecessary database load and is commonly introduced by ORMs.  
> Batching or joins solve this.

***

## 1️⃣8️⃣ How do databases scale for read traffic?

**Answer:**

> Databases scale reads using read replicas.  
> The primary handles writes while replicas serve read queries, trading off slight replication lag for higher throughput.

***

## 1️⃣9️⃣ Why is scaling writes harder than reads?

**Answer:**

> Writes require strong consistency, ordering, and conflict handling.  
> This makes horizontal write scaling complex and usually requires sharding, which introduces application‑level complexity.

***

## 2️⃣0️⃣ What is sharding?

**Answer:**

> Sharding is horizontal partitioning where data is split across multiple databases based on a shard key.  
> It enables write scalability but increases application complexity and makes joins and transactions harder.

***

## 2️⃣1️⃣ What makes a good shard key?

**Answer:**

> A good shard key is evenly distributed, immutable, and avoids hotspots.  
> Common examples include `user_id` or `tenant_id`.

***

## 2️⃣2️⃣ How does caching help database performance?

**Answer:**

> Caching reduces database load by serving frequently accessed data from memory.  
> It improves latency but is not a replacement for proper database design.

***

## 2️⃣3️⃣ When should you optimize database queries?

**Answer:**

> Queries should be optimized when they become a bottleneck based on metrics like latency, CPU usage, or slow query logs.  
> Premature optimization without evidence often causes unnecessary complexity.

***

## 2️⃣4️⃣ What are common database interview red flags?

**Answer:**

> Saying “indexes fix everything,” relying only on hardware scaling, ignoring access patterns, or assuming strong consistency is always required are common red flags.

***

## ✅ Final Interview‑Ready Summary

> **Good database design is about modeling data correctly, optimizing access patterns, and making conscious trade‑offs between consistency, performance, and scalability.**

