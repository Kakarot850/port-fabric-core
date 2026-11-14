package net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction;

import java.util.ArrayList;

import org.jspecify.annotations.Nullable;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.transfer.transaction.SnapshotJournal}: base class for
 * transaction-aware state.
 *
 * <p>Call {@link #updateSnapshots(TransactionContext)} before every change. The journal then takes at most one
 * {@link #createSnapshot() snapshot} per transaction depth, {@link #revertToSnapshot reverts} to it when that
 * transaction aborts, and calls {@link #onRootCommit} once the root transaction has committed.
 *
 * <p>Port: NeoForge's algorithm (including passing the state from before the root transaction to
 * {@link #onRootCommit}, which Fabric's {@code SnapshotParticipant#onFinalCommit} does not) driven by Fabric's
 * close and outer-close callbacks.
 */
public abstract class SnapshotJournal<T extends @Nullable Object> {
    private static final Object NO_SNAPSHOT = new Object();

    private final ArrayList<T> snapshots = new ArrayList<>();
    private final net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext.CloseCallback closeCallback = this::onClose;
    private final net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext.OuterCloseCallback rootCommitCallback = result -> callOnRootCommit();

    private @Nullable T originalState = null;

    /**
     * {@return a new snapshot of the current state} Called at most once per journal and transaction depth.
     */
    protected abstract T createSnapshot();

    /**
     * Restores the state captured in {@code snapshot}.
     */
    protected abstract void revertToSnapshot(T snapshot);

    /**
     * Called when a snapshot is no longer used, e.g. to return it to a pool.
     */
    protected void releaseSnapshot(T snapshot) {}

    /**
     * Called after the root transaction committed, with the state from before the transaction.
     * Changes that must only happen once (notifications, marking dirty, ...) belong here.
     */
    protected void onRootCommit(T originalState) {}

    /**
     * Records the current state in {@code transaction} if it was not recorded at its depth yet. Must be called
     * before the state changes.
     */
    @SuppressWarnings("unchecked")
    public void updateSnapshots(TransactionContext transaction) {
        net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext fabric = transaction.fabric();
        int currentDepth = fabric.nestingDepth();

        snapshots.ensureCapacity(currentDepth + 1);
        for (int i = snapshots.size(); i <= currentDepth; i++) {
            snapshots.add((T) NO_SNAPSHOT);
        }

        if (snapshots.get(currentDepth) == NO_SNAPSHOT) {
            snapshots.set(currentDepth, createSnapshot());
            // Fabric validates here that the transaction is open (NeoForge: Transaction#validateOpen)
            fabric.addCloseCallback(closeCallback);
        }
    }

    private void onClose(net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction,
                         net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext.Result result) {
        int currentDepth = transaction.nestingDepth();

        // Get and remove the relevant snapshot.
        T snapshot = snapshots.remove(currentDepth);

        if (result.wasAborted()) {
            revertToSnapshot(snapshot);
            releaseSnapshot(snapshot);
        } else if (currentDepth <= 0) {
            if (originalState == null) {
                originalState = snapshot;
                transaction.addOuterCloseCallback(rootCommitCallback);
            } else {
                // An onRootCommit callback is already scheduled (this journal was modified in a transaction opened
                // from an onRootCommit callback); it will run with the older original state.
                releaseSnapshot(snapshot);
            }
        } else if (snapshots.get(currentDepth - 1) == NO_SNAPSHOT) {
            // No snapshot yet, so move the snapshot one depth up.
            snapshots.set(currentDepth - 1, snapshot);
            transaction.getOpenTransaction(currentDepth - 1).addCloseCallback(closeCallback);
        } else {
            // There is already an older snapshot at the depth above, just release the newer one.
            releaseSnapshot(snapshot);
        }
    }

    private void callOnRootCommit() {
        // Only scheduled from onClose() when the root transaction committed, so originalState is the first snapshot.
        T originalState = this.originalState;
        // Cleared first: onRootCommit may open new transactions that schedule this journal again.
        this.originalState = null;
        onRootCommit(originalState);
        releaseSnapshot(originalState);
    }
}
