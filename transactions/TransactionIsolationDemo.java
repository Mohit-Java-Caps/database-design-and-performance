package transactions;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TransactionIsolationDemo — All 4 isolation levels with real anomaly simulation.
 *
 * PRODUCTION SCENARIO:
 * ─────────────────────────────────────────────────────────────────────
 * Two users read a bank balance simultaneously.
 * Both see $1000. Both withdraw $800. Both succeed.
 * Balance goes negative. System is broken.
 * Root cause: wrong transaction isolation level.
 *
 * THE 4 ISOLATION LEVELS (weakest → strongest):
 * ─────────────────────────────────────────────────────────────────────
 *  READ UNCOMMITTED  → Can read dirty data (uncommitted changes)
 *                      Anomalies: Dirty Read, Non-repeatable Read, Phantom Read
 *                      Use: Almost never. Performance testing only.
 *
 *  READ COMMITTED    → Only reads committed data (default in PostgreSQL, Oracle)
 *                      Anomalies: Non-repeatable Read, Phantom Read
 *                      Use: Most OLTP applications ✓
 *
 *  REPEATABLE READ   → Same row returns same value within a transaction (default MySQL InnoDB)
 *                      Anomalies: Phantom Read
 *                      Use: Financial reports, consistent snapshots ✓
 *
 *  SERIALIZABLE      → Fully isolated — transactions execute as if sequential
 *                      Anomalies: None
 *                      Use: Critical financial ops, inventory reservations ✓
 *                      Cost: Highest — may cause timeouts and rollbacks under load
 *
 * THREE ANOMALIES:
 * ─────────────────────────────────────────────────────────────────────
 *  DIRTY READ        → Read uncommitted data that may be rolled back
 *  NON-REPEATABLE    → Same SELECT returns different values within one transaction
 *  PHANTOM READ      → Same range query returns different rows within one transaction
 *
 * Author: Mohit Kumar — github.com/Mohit-Java-Caps
 */
public class TransactionIsolationDemo {

    // Simulated shared bank account
    static volatile int accountBalance  = 1000;
    static volatile int uncommittedBalance = 1000; // "dirty" in-progress value
    static volatile boolean transactionRolledBack = false;

    static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    static final AtomicInteger          txId = new AtomicInteger(1);

    // ── Anomaly 1: Dirty Read ─────────────────────────────────────────────
    static void demoDirtyRead() throws InterruptedException {
        System.out.println("━━━ Anomaly 1: DIRTY READ ━━━");
        System.out.println("Transaction A modifies balance (not yet committed).");
        System.out.println("Transaction B reads it — sees dirty/invalid data.\n");

        accountBalance        = 1000;
        uncommittedBalance    = 1000;
        transactionRolledBack = false;

        CountDownLatch latch = new CountDownLatch(1);

        // Transaction A: Debit $500, then rollback
        Thread txA = new Thread(() -> {
            System.out.println("[TX-A] Starting — debiting $500...");
            uncommittedBalance = accountBalance - 500; // dirty write, not committed
            System.out.println("[TX-A] Uncommitted balance now: $" + uncommittedBalance);
            latch.countDown(); // let TX-B read the dirty value

            try { Thread.sleep(100); } catch (InterruptedException e) { return; }
            // Simulate rollback
            uncommittedBalance    = accountBalance; // restore
            transactionRolledBack = true;
            System.out.println("[TX-A] ROLLED BACK — balance restored to: $" + accountBalance);
        }, "TX-A");

        Thread txB = new Thread(() -> {
            try {
                latch.await(); // wait for TX-A to write dirty value
                // READ UNCOMMITTED: reads the dirty value
                int seen = uncommittedBalance;
                System.out.println("[TX-B] READ UNCOMMITTED — sees balance: $" + seen
                    + (transactionRolledBack ? " (WRONG — TX-A rolled back!)" : " (dirty)"));
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "TX-B");

        txA.start(); txB.start();
        txA.join(); txB.join();
        System.out.println("Fix: Use READ COMMITTED or higher — B only reads committed values.\n");
    }

    // ── Anomaly 2: Non-Repeatable Read ────────────────────────────────────
    static void demoNonRepeatableRead() throws InterruptedException {
        System.out.println("━━━ Anomaly 2: NON-REPEATABLE READ ━━━");
        System.out.println("Transaction A reads balance twice.");
        System.out.println("Between reads, Transaction B commits a change.");
        System.out.println("A gets different values from the same query.\n");

        accountBalance = 1000;
        CountDownLatch afterFirstRead  = new CountDownLatch(1);
        CountDownLatch afterBcommit    = new CountDownLatch(1);

        Thread txA = new Thread(() -> {
            // First read
            int firstRead = accountBalance;
            System.out.println("[TX-A] First read: $" + firstRead);
            afterFirstRead.countDown();

            try { afterBcommit.await(); } catch (InterruptedException e) { return; }

            // Second read — same transaction, different value!
            int secondRead = accountBalance;
            System.out.println("[TX-A] Second read: $" + secondRead
                + (firstRead != secondRead ? " ← NON-REPEATABLE! Value changed!" : " (same)"));
        }, "TX-A");

        Thread txB = new Thread(() -> {
            try {
                afterFirstRead.await();
                // B commits a deposit between A's two reads
                accountBalance = 1500;
                System.out.println("[TX-B] Committed deposit — balance now: $" + accountBalance);
                afterBcommit.countDown();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "TX-B");

        txA.start(); txB.start();
        txA.join(); txB.join();
        System.out.println("Fix: Use REPEATABLE READ — A's reads are snapshotted at tx start.\n");
    }

    // ── Anomaly 3: Phantom Read ───────────────────────────────────────────
    static void demoPhantomRead() throws InterruptedException {
        System.out.println("━━━ Anomaly 3: PHANTOM READ ━━━");
        System.out.println("Transaction A queries 'orders > $500' twice.");
        System.out.println("Between reads, Transaction B inserts a new qualifying order.");
        System.out.println("A sees a 'phantom' new row on the second query.\n");

        List<Integer> orders = new java.util.ArrayList<>(java.util.Arrays.asList(200, 600, 800, 300));
        CountDownLatch afterFirstQuery = new CountDownLatch(1);
        CountDownLatch afterInsert     = new CountDownLatch(1);

        Thread txA = new Thread(() -> {
            long firstCount = orders.stream().filter(o -> o > 500).count();
            System.out.println("[TX-A] First query 'amount > 500': " + firstCount + " orders");
            afterFirstQuery.countDown();

            try { afterInsert.await(); } catch (InterruptedException e) { return; }

            long secondCount = orders.stream().filter(o -> o > 500).count();
            System.out.println("[TX-A] Second query 'amount > 500': " + secondCount + " orders"
                + (secondCount != firstCount ? " ← PHANTOM ROW appeared!" : ""));
        }, "TX-A");

        Thread txB = new Thread(() -> {
            try {
                afterFirstQuery.await();
                orders.add(1200); // new high-value order inserted
                System.out.println("[TX-B] Inserted new order $1200 — committed");
                afterInsert.countDown();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "TX-B");

        txA.start(); txB.start();
        txA.join(); txB.join();
        System.out.println("Fix: Use SERIALIZABLE — range queries are locked, no phantoms.\n");
    }

    // ── Isolation level reference table ───────────────────────────────────
    static void printIsolationLevelTable() {
        System.out.println("━━━ Isolation Level Quick Reference ━━━\n");
        System.out.printf("%-22s %-14s %-20s %-14s %-12s%n",
            "Isolation Level", "Dirty Read", "Non-Repeatable", "Phantom", "Performance");
        System.out.println("─".repeat(86));
        System.out.printf("%-22s %-14s %-20s %-14s %-12s%n",
            "READ UNCOMMITTED",  "Possible ✗",  "Possible ✗",     "Possible ✗",  "Highest");
        System.out.printf("%-22s %-14s %-20s %-14s %-12s%n",
            "READ COMMITTED",    "Prevented ✓", "Possible ✗",     "Possible ✗",  "High");
        System.out.printf("%-22s %-14s %-20s %-14s %-12s%n",
            "REPEATABLE READ",   "Prevented ✓", "Prevented ✓",    "Possible ✗",  "Medium");
        System.out.printf("%-22s %-14s %-20s %-14s %-12s%n",
            "SERIALIZABLE",      "Prevented ✓", "Prevented ✓",    "Prevented ✓", "Lowest");

        System.out.println("""

            Spring Boot: @Transactional(isolation = Isolation.READ_COMMITTED)
            PostgreSQL default: READ COMMITTED
            MySQL InnoDB default: REPEATABLE READ (with MVCC — no phantom reads in practice)
            """);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Transaction Isolation Levels Demo ===\n");
        demoDirtyRead();
        demoNonRepeatableRead();
        demoPhantomRead();
        printIsolationLevelTable();
    }
}
