package net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.transfer.transaction.TransactionContext}: a transaction that
 * journals can record their state in ({@link SnapshotJournal#updateSnapshots(TransactionContext)}) and open inner
 * transactions from, but cannot close.
 *
 * <p>Port: backed by Fabric's transaction stack, so NeoForge-style handlers and Fabric {@code Storage}s take part in
 * the same transactions. {@link #fabric()} and {@link #of(net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext)}
 * convert at Transfer API boundaries; both are additions to NeoForge's API.
 */
public sealed interface TransactionContext permits Transaction, FabricTransactionContextView {
    /**
     * {@return the depth of this transaction: 0 for the root, 1 or more for inner transactions}
     *
     * @throws IllegalStateException if called on another thread than the one the transaction was opened on
     */
    default int depth() {
        return fabric().nestingDepth();
    }

    /**
     * {@return the Fabric transaction this context is backed by}
     */
    net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext fabric();

    /**
     * {@return a view of a Fabric transaction, for calling NeoForge-style handlers from Fabric {@code Storage} code}
     */
    static TransactionContext of(net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext fabricContext) {
        if (fabricContext instanceof net.fabricmc.fabric.api.transfer.v1.transaction.Transaction fabricTransaction) {
            return new FabricTransactionContextView(fabricTransaction);
        }
        return new FabricTransactionContextView(fabricContext.getOpenTransaction(fabricContext.nestingDepth()));
    }
}
