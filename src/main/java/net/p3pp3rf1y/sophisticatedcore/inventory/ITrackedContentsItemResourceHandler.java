package net.p3pp3rf1y.sophisticatedcore.inventory;

import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.ResourceHandler;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;

import java.util.Set;
import java.util.function.Consumer;

public interface ITrackedContentsItemResourceHandler extends ResourceHandler<ItemResource>, ISlotStackAccessor, IInsertBlockOverride {

	Set<ItemStackKey> getTrackedStacks();

	void registerTrackingListeners(Consumer<ItemStackKey> onAddStackKey, Consumer<ItemStackKey> onRemoveStackKey, Runnable onAddFirstEmptySlot,
			Runnable onRemoveLastEmptySlot);

	void unregisterStackKeyListeners();

	boolean hasEmptySlots();

	int getInternalSlotLimit(int slot);
}
