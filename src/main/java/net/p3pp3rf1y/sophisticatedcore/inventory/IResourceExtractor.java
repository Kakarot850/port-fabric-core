package net.p3pp3rf1y.sophisticatedcore.inventory;

import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.TransactionContext;

public interface IResourceExtractor {
	int extract(int slot, ItemResource resource, int amount, TransactionContext transaction);
}
