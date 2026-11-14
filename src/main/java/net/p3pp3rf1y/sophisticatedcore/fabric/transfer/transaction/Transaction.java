package net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction;

import org.jspecify.annotations.Nullable;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.transfer.transaction.Transaction}: a global operation that
 * either succeeds as a whole ({@link #commit()}) or is rolled back ({@link #close()} without a commit).
 *
 * <pre>{@code
 * try (Transaction rootTransaction = Transaction.openRoot()) {
 *     try (Transaction innerTransaction = Transaction.open(rootTransaction)) {
 *         innerTransaction.commit(); // applied only if the root commits too
 *     }
 *     rootTransaction.commit();
 * }
 * }</pre>
 *
 * <p>Port: each instance wraps one Fabric {@link net.fabricmc.fabric.api.transfer.v1.transaction.Transaction}; the
 * transaction stack, thread checks and close-callback ordering are Fabric's. NeoForge built its transfer transactions
 * after Fabric's, so the observable rules are the same: one root per thread, {@link #open} requires the current
 * transaction as parent, {@link #close()} aborts only if still open. NeoForge's {@code ROOT_CLOSING} lifecycle is
 * Fabric's {@code OUTER_CLOSING}.
 */
public final class Transaction implements AutoCloseable, TransactionContext {
    private final net.fabricmc.fabric.api.transfer.v1.transaction.Transaction fabric;

    private Transaction(net.fabricmc.fabric.api.transfer.v1.transaction.Transaction fabric) {
        this.fabric = fabric;
    }

    /**
     * Opens a new transaction without a parent: a root transaction.
     *
     * @throws IllegalStateException if a transaction is already open or closing on the current thread
     */
    public static Transaction openRoot() {
        return new Transaction(net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter());
    }

    /**
     * Opens a new transaction with the given parent, or a root transaction if {@code parent} is {@code null}.
     *
     * @throws IllegalStateException if no parent is passed but a transaction is already open, or if the parent is
     *                               not the current transaction
     */
    public static Transaction open(@Nullable TransactionContext parent) {
        return new Transaction(parent == null
                ? net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()
                : parent.fabric().openNested());
    }

    /**
     * {@return the current lifecycle of the transaction stack on this thread}
     */
    public static Lifecycle getLifecycle() {
        return switch (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.getLifecycle()) {
            case NONE -> Lifecycle.NONE;
            case OPEN -> Lifecycle.OPEN;
            case CLOSING -> Lifecycle.CLOSING;
            case OUTER_CLOSING -> Lifecycle.ROOT_CLOSING;
        };
    }

    /**
     * Only for code that takes part in a transaction but cannot receive its context.
     *
     * @return the current transaction on this thread, or {@code null} if none is open
     * @throws IllegalStateException when called while a transaction is closing
     * @deprecated pass the transaction context instead whenever possible
     */
    @Deprecated
    public static @Nullable TransactionContext getCurrentOpenedTransaction() {
        @SuppressWarnings("deprecation")
        net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext current =
                net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.getCurrentUnsafe();
        return current == null ? null : TransactionContext.of(current);
    }

    /**
     * Commits the changes of this transaction and its committed inner transactions. For a root transaction they are
     * applied; for an inner transaction they are applied when and if all parents commit.
     *
     * @throws IllegalStateException if called on another thread, on a transaction that is not the current one, or on
     *                               a closed transaction
     */
    public void commit() {
        fabric.commit();
    }

    /**
     * Aborts this transaction if it was not closed already.
     */
    @Override
    public void close() {
        fabric.close();
    }

    @Override
    public net.fabricmc.fabric.api.transfer.v1.transaction.Transaction fabric() {
        return fabric;
    }

    @Override
    public String toString() {
        return "Transaction[depth=%d]".formatted(depth());
    }

    /**
     * The possible states of the transaction stack.
     */
    public enum Lifecycle {
        /** No transaction is open or closing. */
        NONE,
        /** A transaction is open. */
        OPEN,
        /** The current transaction is being closed. */
        CLOSING,
        /** {@link SnapshotJournal#onRootCommit} callbacks are running; no transaction is open or closing. */
        ROOT_CLOSING
    }
}
