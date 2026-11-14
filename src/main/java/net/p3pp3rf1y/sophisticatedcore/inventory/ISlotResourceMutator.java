package net.p3pp3rf1y.sophisticatedcore.inventory;

import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;

@FunctionalInterface
public interface ISlotResourceMutator {
	void set(int slot, ItemResource res, int amount);
}
