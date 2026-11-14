package net.p3pp3rf1y.sophisticatedcore.fabric.transfer.bridge;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.p3pp3rf1y.sophisticatedcore.fabric.fluids.FluidType;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.fluid.FluidResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.resource.Resource;

/**
 * Port addition: converts resources and amounts between the NeoForge-style transfer stand-ins and Fabric's Transfer
 * API. Items use the same unit on both sides; fluids are int millibuckets here and long droplets in Fabric
 * ({@link FluidType#DROPLETS_PER_MB} per mB).
 *
 * @param <R> NeoForge-style resource
 * @param <V> Fabric variant
 */
public interface ResourceConverter<R extends Resource, V extends TransferVariant<?>> {
	ResourceConverter<ItemResource, ItemVariant> ITEM = new ResourceConverter<>() {
		@Override
		public ItemVariant toVariant(ItemResource resource) {
			return resource.isEmpty() ? ItemVariant.blank() : ItemVariant.of(resource.getItem(), resource.getComponentsPatch());
		}

		@Override
		public ItemResource toResource(ItemVariant variant) {
			return variant.isBlank() ? ItemResource.EMPTY : ItemResource.of(variant.getItem(), variant.getComponentsPatch());
		}

		@Override
		public long unitsPerAmount() {
			return 1;
		}
	};

	ResourceConverter<FluidResource, FluidVariant> FLUID = new ResourceConverter<>() {
		@Override
		public FluidVariant toVariant(FluidResource resource) {
			// FluidStack#toVariant maps flowing fluids to their source, which FluidVariant requires
			return resource.isEmpty() ? FluidVariant.blank() : resource.toStack(1).toVariant();
		}

		@Override
		public FluidResource toResource(FluidVariant variant) {
			return variant.isBlank() ? FluidResource.EMPTY : FluidResource.of(variant.getFluid(), variant.getComponentsPatch());
		}

		@Override
		public long unitsPerAmount() {
			return FluidType.DROPLETS_PER_MB;
		}
	};

	V toVariant(R resource);

	R toResource(V variant);

	/**
	 * {@return how many Fabric units one unit of this side's amount is}
	 */
	long unitsPerAmount();

	/**
	 * {@return a NeoForge-style amount as Fabric units}
	 */
	default long toFabricAmount(long amount) {
		return amount * unitsPerAmount();
	}

	/**
	 * {@return Fabric units as a NeoForge-style amount, rounded down}
	 */
	default long fromFabricAmount(long fabricAmount) {
		return fabricAmount / unitsPerAmount();
	}

	/**
	 * {@return Fabric units as a NeoForge-style int amount, rounded down and clamped to {@code Integer.MAX_VALUE}}
	 */
	default int fromFabricAmountClamped(long fabricAmount) {
		return (int) Math.min(Integer.MAX_VALUE, fromFabricAmount(fabricAmount));
	}
}
