package net.p3pp3rf1y.sophisticatedcore.fabric.transfer.bridge;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.ResourceHandler;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.TransferPreconditions;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.fluid.FluidResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.resource.Resource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Port addition: exposes a Fabric {@link Storage} as a NeoForge-style {@link ResourceHandler}.
 *
 * <p>Indexes are the slots of a {@link SlottedStorage}. Other storages only expose views, so their indexes are the
 * views in iteration order, re-read on every {@link #size()} call; inserting at such an index inserts into the
 * storage as a whole (views cannot accept insertions). {@link #isValid} simulates inserting one unit.
 *
 * <p>Fluid amounts are converted droplets ↔ mB (rounded down). Transfers that would move a fraction of a millibucket
 * are retried with the whole-millibucket part, so no droplets are lost or created.
 */
public final class StorageResourceHandler<R extends Resource, V extends TransferVariant<?>> implements ResourceHandler<R> {
	private final Storage<V> storage;
	private final ResourceConverter<R, V> converter;
	private final @Nullable SlottedStorage<V> slotted;
	private @Nullable List<StorageView<V>> views;

	private StorageResourceHandler(Storage<V> storage, ResourceConverter<R, V> converter) {
		this.storage = storage;
		this.converter = converter;
		this.slotted = storage instanceof SlottedStorage<V> s ? s : null;
	}

	/**
	 * {@return {@code storage} as an item resource handler} Unwraps storages created by {@link ResourceHandlerStorage}.
	 */
	public static ResourceHandler<ItemResource> ofItems(Storage<ItemVariant> storage) {
		return of(storage, ResourceConverter.ITEM);
	}

	/**
	 * {@return {@code storage} as a fluid resource handler} Unwraps storages created by {@link ResourceHandlerStorage}.
	 */
	public static ResourceHandler<FluidResource> ofFluids(Storage<FluidVariant> storage) {
		return of(storage, ResourceConverter.FLUID);
	}

	@SuppressWarnings("unchecked")
	private static <R extends Resource, V extends TransferVariant<?>> ResourceHandler<R> of(Storage<V> storage, ResourceConverter<R, V> converter) {
		if (storage instanceof ResourceHandlerStorage<?, ?> wrapper && wrapper.converter() == converter) {
			return (ResourceHandler<R>) wrapper.handler();
		}
		return new StorageResourceHandler<>(storage, converter);
	}

	Storage<V> storage() {
		return storage;
	}

	ResourceConverter<R, V> converter() {
		return converter;
	}

	@Override
	public int size() {
		if (slotted != null) {
			return slotted.getSlotCount();
		}
		List<StorageView<V>> list = new ArrayList<>();
		storage.iterator().forEachRemaining(list::add);
		views = list;
		return list.size();
	}

	private StorageView<V> view(int index) {
		if (slotted != null) {
			return slotted.getSlot(index);
		}
		if (views == null) {
			size();
		}
		return views.get(index);
	}

	private Storage<V> insertTarget(int index) {
		return slotted != null ? slotted.getSlot(index) : storage;
	}

	@Override
	public R getResource(int index) {
		return converter.toResource(view(index).getResource());
	}

	@Override
	public long getAmountAsLong(int index) {
		return converter.fromFabricAmount(view(index).getAmount());
	}

	@Override
	public long getCapacityAsLong(int index, R resource) {
		return converter.fromFabricAmount(view(index).getCapacity());
	}

	@Override
	public boolean isValid(int index, R resource) {
		if (resource.isEmpty()) {
			return false;
		}
		Transaction.Lifecycle lifecycle = Transaction.getLifecycle();
		if (lifecycle == Transaction.Lifecycle.CLOSING) {
			return true; // no transaction can be opened while one is closing; assume valid
		}
		@SuppressWarnings("deprecation")
		net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext current =
				lifecycle == Transaction.Lifecycle.OPEN ? Transaction.getCurrentUnsafe() : null;
		try (Transaction simulation = Transaction.openNested(current)) {
			return insertTarget(index).insert(converter.toVariant(resource), converter.unitsPerAmount(), simulation) > 0;
		}
	}

	@Override
	public int insert(int index, R resource, int amount, TransactionContext transaction) {
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		return insertExact(insertTarget(index), resource, amount, transaction);
	}

	@Override
	public int insert(R resource, int amount, TransactionContext transaction) {
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		return insertExact(storage, resource, amount, transaction);
	}

	@Override
	public int extract(int index, R resource, int amount, TransactionContext transaction) {
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		StorageView<V> view = view(index);
		return moveExact(amount, transaction, (units, tx) -> view.extract(converter.toVariant(resource), units, tx));
	}

	@Override
	public int extract(R resource, int amount, TransactionContext transaction) {
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		return moveExact(amount, transaction, (units, tx) -> storage.extract(converter.toVariant(resource), units, tx));
	}

	private int insertExact(Storage<V> target, R resource, int amount, TransactionContext transaction) {
		return moveExact(amount, transaction, (units, tx) -> target.insert(converter.toVariant(resource), units, tx));
	}

	/**
	 * Runs {@code move} for {@code amount} (converted to Fabric units) and returns the moved amount. If the Fabric side
	 * moved a fraction of a unit of this side, the move is redone in a fresh nested transaction for the whole part.
	 */
	private int moveExact(int amount, TransactionContext transaction, Move move) {
		if (amount == 0) {
			return 0;
		}
		long perAmount = converter.unitsPerAmount();
		if (perAmount == 1) {
			return (int) move.apply(amount, transaction.fabric());
		}
		long whole;
		try (Transaction attempt = Transaction.openNested(transaction.fabric())) {
			long moved = move.apply(converter.toFabricAmount(amount), attempt);
			whole = moved / perAmount * perAmount;
			if (moved == whole) {
				attempt.commit();
				return (int) (whole / perAmount);
			}
		}
		if (whole == 0) {
			return 0;
		}
		try (Transaction retry = Transaction.openNested(transaction.fabric())) {
			long moved = move.apply(whole, retry);
			if (moved != whole) {
				return 0;
			}
			retry.commit();
			return (int) (whole / perAmount);
		}
	}

	@FunctionalInterface
	private interface Move {
		long apply(long units, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction);
	}

	@Override
	public String toString() {
		return "StorageResourceHandler[" + storage + "]";
	}
}
