package net.p3pp3rf1y.sophisticatedcore.extensions.inventory;

import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;

/**
 * Methods NeoForge patches into {@link Slot} ({@code getSlotIndex()}, {@code isSameInventory(Slot)},
 * {@code setBackground(Identifier)}, which {@code getNoItemIcon()} then returns), added on Fabric through Loom
 * interface injection and implemented by {@code mixin.common.SlotMixin}. Subclasses may override them, as on NeoForge.
 * {@code tools/port_copy.py} renames ported call sites and overrides to the {@code sophisticatedCore_} names.
 */
public interface SophisticatedSlot {
	/**
	 * {@return the index of this slot in its container} Unlike {@link Slot#getContainerSlot()}, NeoForge's original
	 * is not overridden by vanilla subclasses.
	 */
	default int sophisticatedCore_getSlotIndex() {
		throw new UnsupportedOperationException("Implemented by SlotMixin");
	}

	/**
	 * {@return whether {@code other} belongs to the same container as this slot}
	 */
	default boolean sophisticatedCore_isSameInventory(Slot other) {
		throw new UnsupportedOperationException("Implemented by SlotMixin");
	}

	/**
	 * Sets the sprite {@link Slot#getNoItemIcon()} returns (unless a subclass overrides that method).
	 *
	 * @return this slot, for chaining
	 */
	default Slot sophisticatedCore_setBackground(Identifier sprite) {
		throw new UnsupportedOperationException("Implemented by SlotMixin");
	}
}
