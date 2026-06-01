package indexing;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DatabaseIndexingDemo — N+1 problem, index impact, and query optimization.
 *
 * PRODUCTION SCENARIO:
 * ─────────────────────────────────────────────────────────────────────
 * Your API response time jumps from 50ms to 8 seconds after a data
 * migration. Thread dump shows threads WAITING on JDBC. DB CPU spikes.
 * Root cause: missing index + N+1 query problem.
 *
 * This demo simulates (in-memory) what happens with and without indexes,
 * and demonstrates the N+1 problem with its fix.
 *
 * WHAT IS AN INDEX?
 * ─────────────────────────────────────────────────────────────────────
 * A database index is a separate data structure (usually B-Tree) that
 * maps column values → row locations.
 *
 * Without index: Full table scan — O(n) — reads every row
 * With index:    B-Tree lookup  — O(log n) — jumps directly to the row
 *
 * THE N+1 PROBLEM:
 * ─────────────────────────────────────────────────────────────────────
 * You fetch N orders (1 query).
 * Then for each order, you fetch the customer (N queries).
 * Total: N+1 queries. For 1000 orders = 1001 DB round trips.
 *
 * Fix: Use a JOIN or batch fetch — 1 query total.
 *
 * Author: Mohit Kumar — github.com/Mohit-Java-Caps
 */
public class DatabaseIndexingDemo {

    record Customer(int id, String name, String email, String city) {}
    record Order(int id, int customerId, String product, double amount, String status) {}

    // ── Simulated database tables ─────────────────────────────────────────
    static final List<Customer> CUSTOMERS = new ArrayList<>();
    static final List<Order>    ORDERS    = new ArrayList<>();

    // Simulated indexes (HashMap = O(1) lookup, like a DB hash index)
    static final Map<Integer, Customer>       CUSTOMER_BY_ID     = new HashMap<>();
    static final Map<String, List<Customer>>  CUSTOMER_BY_CITY   = new HashMap<>();
    static final Map<Integer, List<Order>>    ORDERS_BY_CUSTOMER = new HashMap<>();
    static final Map<String, List<Order>>     ORDERS_BY_STATUS   = new HashMap<>();

    static {
        // Seed 10,000 customers
        String[] cities = {"Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata"};
        Random rnd = new Random(42);
        for (int i = 1; i <= 10_000; i++) {
            String city = cities[rnd.nextInt(cities.length)];
            Customer c = new Customer(i, "Customer-" + i, "user" + i + "@example.com", city);
            CUSTOMERS.add(c);

            // Build indexes
            CUSTOMER_BY_ID.put(c.id(), c);
            CUSTOMER_BY_CITY.computeIfAbsent(c.city(), k -> new ArrayList<>()).add(c);
        }

        // Seed 50,000 orders
        String[] products  = {"Laptop", "Phone", "Tablet", "Monitor", "Keyboard"};
        String[] statuses  = {"PENDING", "SHIPPED", "DELIVERED", "CANCELLED"};
        for (int i = 1; i <= 50_000; i++) {
            int custId = rnd.nextInt(10_000) + 1;
            String status = statuses[rnd.nextInt(statuses.length)];
            Order o = new Order(i, custId, products[rnd.nextInt(products.length)],
                50 + rnd.nextDouble() * 2000, status);
            ORDERS.add(o);

            // Build indexes
            ORDERS_BY_CUSTOMER.computeIfAbsent(custId, k -> new ArrayList<>()).add(o);
            ORDERS_BY_STATUS.computeIfAbsent(status, k -> new ArrayList<>()).add(o);
        }
    }

    // ── Demo 1: Full table scan vs index lookup ───────────────────────────
    static void demo1_IndexImpact() {
        System.out.println("━━━ Demo 1: Full Table Scan vs Index Lookup ━━━\n");

        // WITHOUT index: scan every row (O(n))
        long start = System.nanoTime();
        Customer found = null;
        for (Customer c : CUSTOMERS) {
            if (c.id() == 7777) { found = c; break; }
        }
        long scanMs = (System.nanoTime() - start) / 1_000;
        System.out.println("Without index (full scan):  found=" + found.name() + " in " + scanMs + "μs");

        // WITH index: direct lookup (O(1) hash / O(log n) B-tree)
        start = System.nanoTime();
        Customer indexed = CUSTOMER_BY_ID.get(7777);
        long idxMs = (System.nanoTime() - start) / 1_000;
        System.out.println("With index    (hash lookup): found=" + indexed.name() + " in " + idxMs + "μs");
        System.out.println("Speedup: ~" + Math.max(1, scanMs / Math.max(1, idxMs)) + "x faster with index");
    }

    // ── Demo 2: N+1 problem ───────────────────────────────────────────────
    static void demo2_NPlus1Problem() {
        System.out.println("\n━━━ Demo 2: The N+1 Query Problem ━━━\n");

        // Fetch first 20 orders
        List<Order> orders = ORDERS.subList(0, 20);

        // BAD: N+1 — fetch each customer separately (simulates separate DB queries)
        long start = System.nanoTime();
        int queryCount = 1; // 1 for the initial orders fetch
        List<String> results1 = new ArrayList<>();
        for (Order o : orders) {
            // This simulates: SELECT * FROM customers WHERE id = ?  (per order!)
            Customer c = null;
            for (Customer customer : CUSTOMERS) { // full scan each time — worst case
                if (customer.id() == o.customerId()) { c = customer; break; }
            }
            results1.add(o.product() + " → " + (c != null ? c.name() : "?"));
            queryCount++;
        }
        long nplus1Time = (System.nanoTime() - start) / 1_000_000;
        System.out.println("N+1 approach: " + queryCount + " queries | Time: " + nplus1Time + "ms");
        System.out.println("  First 3: " + results1.subList(0, 3));

        // GOOD: Batch fetch — collect all customer IDs, fetch in one query
        start = System.nanoTime();
        Set<Integer> customerIds = orders.stream()
            .map(Order::customerId).collect(Collectors.toSet());
        // 1 query: SELECT * FROM customers WHERE id IN (...)
        Map<Integer, Customer> customerMap = customerIds.stream()
            .map(CUSTOMER_BY_ID::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Customer::id, c -> c));
        // Now join in memory
        List<String> results2 = orders.stream()
            .map(o -> o.product() + " → " + customerMap.get(o.customerId()).name())
            .toList();
        long batchTime = (System.nanoTime() - start) / 1_000_000;
        System.out.println("\nBatch approach: 2 queries | Time: " + batchTime + "ms");
        System.out.println("  First 3: " + results2.subList(0, 3));
        System.out.println("\nWith 1000 orders: N+1 = 1001 DB round trips, Batch = 2 queries.");
        System.out.println("In JPA: use @EntityGraph or JOIN FETCH to avoid N+1 automatically.");
    }

    // ── Demo 3: Composite index for range + filter queries ────────────────
    static void demo3_QueryOptimization() {
        System.out.println("\n━━━ Demo 3: Query Optimization Patterns ━━━\n");

        // Query: DELIVERED orders worth more than $1000
        long start = System.nanoTime();
        // WITHOUT proper index — full scan
        List<Order> fullScan = ORDERS.stream()
            .filter(o -> "DELIVERED".equals(o.status()) && o.amount() > 1000)
            .toList();
        long fullScanTime = (System.nanoTime() - start) / 1_000;

        // WITH index on status — use status index to narrow, then filter amount
        start = System.nanoTime();
        List<Order> withIndex = ORDERS_BY_STATUS.getOrDefault("DELIVERED", List.of())
            .stream()
            .filter(o -> o.amount() > 1000)
            .toList();
        long indexTime = (System.nanoTime() - start) / 1_000;

        System.out.println("Query: status='DELIVERED' AND amount > 1000");
        System.out.printf("  Full scan:    %5d results in %dμs (scanned all %d rows)%n",
            fullScan.size(), fullScanTime, ORDERS.size());
        System.out.printf("  With index:   %5d results in %dμs (scanned ~%d rows)%n",
            withIndex.size(), indexTime,
            ORDERS_BY_STATUS.getOrDefault("DELIVERED", List.of()).size());

        System.out.println("\nSQL equivalents:");
        System.out.println("  Slow:    SELECT * FROM orders WHERE status='DELIVERED' AND amount > 1000");
        System.out.println("           → Full table scan if no index");
        System.out.println("  Fast:    CREATE INDEX idx_orders_status ON orders(status);");
        System.out.println("           CREATE INDEX idx_orders_status_amount ON orders(status, amount);");
        System.out.println("           → Composite index handles both filters in one B-tree lookup");

        System.out.println("\nIndex design rules:");
        System.out.println("  1. Index columns used in WHERE, JOIN, ORDER BY");
        System.out.println("  2. Composite index: most selective column first");
        System.out.println("  3. Never index columns with low cardinality (e.g. boolean flags)");
        System.out.println("  4. EXPLAIN ANALYZE in PostgreSQL shows index usage");
        System.out.println("  5. Too many indexes slow down INSERT/UPDATE — choose wisely");
    }

    public static void main(String[] args) {
        System.out.println("=== Database Indexing & Query Optimization Demo ===");
        System.out.println("Dataset: 10,000 customers · 50,000 orders\n");

        demo1_IndexImpact();
        demo2_NPlus1Problem();
        demo3_QueryOptimization();

        System.out.println("""

            === Key Interview Points ===
            N+1 in JPA: use JOIN FETCH or @EntityGraph — never lazy-load in a loop.
            EXPLAIN ANALYZE (PostgreSQL) / EXPLAIN (MySQL) shows if index is used.
            Partial indexes: CREATE INDEX ON orders(status) WHERE status != 'DELIVERED'
            Covering indexes: include all queried columns — avoids table lookup entirely.
            """);
    }
}
