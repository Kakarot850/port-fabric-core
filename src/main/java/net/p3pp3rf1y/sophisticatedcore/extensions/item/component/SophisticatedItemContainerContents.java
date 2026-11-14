package net.p3pp3rf1y.sophisticatedcore.extensions.item.component;

import net.minecraft.world.item.ItemStack;

/**
 * Methods NeoForge patches into {@code ItemContainerContents} ({@code getSlots()}, {@code getStackInSlot(int)}),
 * added on Fabric through Loom interface injection (fabric.mod.json {@code loom:injected_interfaces}) and implemented by
 * {@code mixin.common.ItemContainerContentsMixin}. Names carry the {@code sophisticatedCore_} prefix, as in
 * ref-fabric-core, so they cannot clash with other mods injecting into the same class. Ported call sites
 * {@code contents.getSlots()} become {@code contents.sophisticatedCore_getSlots()}.
 */
public interface SophisticatedItemContainerContents {
	default int sophisticatedCore_getSlots() {
		throw new UnsupportedOperationException("Implemented by ItemContainerContentsMixin");
	}

	default ItemStack sophisticatedCore_getStackInSlot(int slot) {
		throw new UnsupportedOperationException("Implemented by ItemContainerContentsMixin");
	}
}
