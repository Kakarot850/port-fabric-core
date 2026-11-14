package net.p3pp3rf1y.sophisticatedcore.fabric.transfer.bridge;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.ResourceHandler;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.fluid.FluidResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.resource.Resource;

import java.util.Collections;
import java.util.Iterator;

/**
 * Port addition: exposes a NeoForge-style {@link ResourceHandler} as a Fabric {@link SlottedStorage} (one Fabric slot
 * per handler index), so other Fabric mods can use our handlers. Both sides share Fabric's transaction stack, so the
 * Fabric transaction is passed straight through ({@link net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.TransactionContext#of}).
 * Fluid amounts are converted mB ↔ droplets; requests below 1 mB move nothing.
 */
public final class ResourceHandlerStorage<R extends Resource, V extends TransferVariant<?>> implements SlottedStorage<V> {
	private final ResourceHandler<R> handler;
	private final ResourceConverter<R, V> converter;

	private ResourceHandlerStorage(ResourceHandler<R> handler, ResourceConverter<R, V> converter) {
		this.handler = handler;
		this.converter = converter;
	}

	/**
	 * {@return {@code handler} as a Fabric item storage} Unwraps handlers created by {@link StorageResourceHandler}.
	 */
	public static Storage<ItemVariant> ofItems(ResourceHandler<ItemResource> handler) {
		return of(handler, ResourceConverter.ITEM);
	}

	/**
	 * {@return {@code handler} as a Fabric fluid storage} Unwraps handlers created by {@link StorageResourceHandler}.
	 */
	public static Storage<FluidVariant> ofFluids(ResourceHandler<FluidResource> handler) {
		return of(handler, ResourceConverter.FLUID);
	}

	@SuppressWarnings("unchecked")
	private static <R extends Resource, V extends TransferVariant<?>> Storage<V> of(ResourceHandler<R> handler, ResourceConverter<R, V> converter) {
		if (handler instanceof StorageResourceHandler<?, ?> wrapper && wrapper.converter() == converter) {
			return (Storage<V>) wrapper.storage();
		}
		return new ResourceHandlerStorage<>(handler, converter);
	}

	ResourceHandler<R> handler() {
		return handler;
	}

	ResourceConverter<R, V> converter() {
		return converter;
	}

	@Override
	public long insert(V resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		int amount = converter.fromFabricAmountClamped(maxAmount);
		if (amount == 0) {
			return 0;
		}
		return converter.toFabricAmount(handler.insert(converter.toResource(resource), amount, ctx(transaction)));
	}

	@Override
	public long extract(V resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		int amount = converter.fromFabricAmountClamped(maxAmount);
		if (amount == 0) {
			return 0;
		}
		return converter.toFabricAmount(handler.extract(converter.toResource(resource), amount, ctx(transaction)));
	}

	@Override
	public int getSlotCount() {
		return handler.size();
	}

	@Override
	public SingleSlotStorage<V> getSlot(int slot) {
		return new Slot(slot);
	}

	@Override
	public Iterator<StorageView<V>> iterator() {
		return Collections.<StorageView<V>>unmodifiableList(getSlots()).iterator();
	}

	@Override
	public String toString() {
		return "ResourceHandlerStorage[" + handler + "]";
	}

	private static net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.TransactionContext ctx(TransactionContext transaction) {
		return net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.TransactionContext.of(transaction);
	}

	private final class Slot implements SingleSlotStorage<V> {
		private final int index;

		private Slot(int index) {
			this.index = index;
		}

		@Override
		public long insert(V resource, long maxAmount, TransactionContext transaction) {
			StoragePreconditions.notBlankNotNegative(resource, maxAmount);
			int amount = converter.fromFabricAmountClamped(maxAmount);
			if (amount == 0) {
				return 0;
			}
			return converter.toFabricAmount(handler.insert(index, converter.toResource(resource), amount, ctx(transaction)));
		}

		@Override
		public long extract(V resource, long maxAmount, TransactionContext transaction) {
			StoragePreconditions.notBlankNotNegative(resource, maxAmount);
			int amount = converter.fromFabricAmountClamped(maxAmount);
			if (amount == 0) {
				return 0;
			}
			return converter.toFabricAmount(handler.extract(index, converter.toResource(resource), amount, ctx(transaction)));
		}

		@Override
		public boolean isResourceBlank() {
			return handler.getResource(index).isEmpty();
		}

		@Override
		public V getResource() {
			return converter.toVariant(handler.getResource(index));
		}

		@Override
		public long getAmount() {
			return converter.toFabricAmount(handler.getAmountAsLong(index));
		}

		@Override
		public long getCapacity() {
			return converter.toFabricAmount(handler.getCapacityAsLong(index, handler.getResource(index)));
		}

		@Override
		public String toString() {
			return "ResourceHandlerStorage.Slot[" + index + " of " + handler + "]";
		}
	}
}
