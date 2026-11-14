package net.p3pp3rf1y.sophisticatedcore.upgrades;

import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;

public interface IExtractResponseUpgrade {
	void onAfterExtract(InventoryHandler inventoryHandler, int slot, ItemResource originalResource);
}
