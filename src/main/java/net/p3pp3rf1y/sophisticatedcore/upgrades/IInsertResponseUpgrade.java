package net.p3pp3rf1y.sophisticatedcore.upgrades;

import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.TransactionContext;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;

public interface IInsertResponseUpgrade {
	default int onBeforeInsert(InventoryHandler inventoryHandler, ItemResource resource, int amount) {
		return 0;
	}

	default int onBeforeInsert(InventoryHandler inventoryHandler, int slot, ItemResource resource, int amount) {
		return 0;
	}

	default void onAfterInsert(InventoryHandler inventoryHandler, int slot, TransactionContext tx) {
		// noop by default
	}
}
