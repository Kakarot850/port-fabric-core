package net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction;

/**
 * Port addition: a {@link TransactionContext} view of a transaction that was opened through Fabric's API.
 * It cannot be committed or closed from NeoForge-style code, just like any {@link TransactionContext}.
 */
record FabricTransactionContextView(net.fabricmc.fabric.api.transfer.v1.transaction.Transaction fabric) implements TransactionContext {
    @Override
    public String toString() {
        return "Transaction[depth=%d, fabric]".formatted(depth());
    }
}
