package net.p3pp3rf1y.sophisticatedcore.fabric.transfer.bridge;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.access.ItemAccess;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;

import java.util.List;

/**
 * Port addition: a Fabric {@link ContainerItemContext} backed by a NeoForge-style {@link ItemAccess}, for querying
 * Fabric's item-based lookups ({@code ItemStorage.ITEM}, {@code FluidStorage.ITEM}) with an item access.
 *
 * <p>Both describe "an item somewhere, plus where overflow goes": {@link #insert} / {@link #extract} map to the item
 * access directly (it handles overflow itself). The main slot only accepts items stacking with its current content,
 * up to the max stack size; everything else goes through {@link #insertOverflow}.
 */
public final class ItemAccessContainerItemContext implements ContainerItemContext {
	private final ItemAccess access;
	private final MainSlot mainSlot = new MainSlot();

	public ItemAccessContainerItemContext(ItemAccess access) {
		this.access = access;
	}

	@Override
	public SingleSlotStorage<ItemVariant> getMainSlot() {
		return mainSlot;
	}

	@Override
	public long insert(ItemVariant itemVariant, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(itemVariant, maxAmount);
		return access.insert(ResourceConverter.ITEM.toResource(itemVariant), clamp(maxAmount), ctx(transaction));
	}

	@Override
	public long extract(ItemVariant itemVariant, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(itemVariant, maxAmount);
		return access.extract(ResourceConverter.ITEM.toResource(itemVariant), clamp(maxAmount), ctx(transaction));
	}

	@Override
	public long insertOverflow(ItemVariant itemVariant, long maxAmount, TransactionContext transactionContext) {
		StoragePreconditions.notBlankNotNegative(itemVariant, maxAmount);
		return access.insert(ResourceConverter.ITEM.toResource(itemVariant), clamp(maxAmount), ctx(transactionContext));
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getAdditionalSlots() {
		return List.of();
	}

	@Override
	public String toString() {
		return "ItemAccessContainerItemContext[" + access + "]";
	}

	private static int clamp(long amount) {
		return (int) Math.min(Integer.MAX_VALUE, amount);
	}

	private static net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.TransactionContext ctx(TransactionContext transaction) {
		return net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.TransactionContext.of(transaction);
	}

	private final class MainSlot implements SingleSlotStorage<ItemVariant> {
		@Override
		public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			StoragePreconditions.notBlankNotNegative(resource, maxAmount);
			ItemResource current = access.getResource();
			ItemResource inserted = ResourceConverter.ITEM.toResource(resource);
			if (!current.isEmpty() && !current.equals(inserted)) {
				return 0;
			}
			long space = inserted.getMaxStackSize() - (long) access.getAmount();
			if (space <= 0) {
				return 0;
			}
			return access.insert(inserted, clamp(Math.min(maxAmount, space)), ctx(transaction));
		}

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			StoragePreconditions.notBlankNotNegative(resource, maxAmount);
			return access.extract(ResourceConverter.ITEM.toResource(resource), clamp(maxAmount), ctx(transaction));
		}

		@Override
		public boolean isResourceBlank() {
			return access.getResource().isEmpty();
		}

		@Override
		public ItemVariant getResource() {
			return ResourceConverter.ITEM.toVariant(access.getResource());
		}

		@Override
		public long getAmount() {
			return access.getAmount();
		}

		@Override
		public long getCapacity() {
			ItemResource current = access.getResource();
			return Math.max(access.getAmount(), current.isEmpty() ? 64 : current.getMaxStackSize());
		}
	}
}
