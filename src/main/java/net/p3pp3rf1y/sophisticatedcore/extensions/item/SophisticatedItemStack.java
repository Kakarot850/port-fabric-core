package net.p3pp3rf1y.sophisticatedcore.extensions.item;

import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.fabric.capabilities.ItemCapability;
import org.jspecify.annotations.Nullable;

/**
 * NeoForge's {@code IItemStackExtension#getCapability} overloads, added to {@link ItemStack} through Loom interface
 * injection ({@code mixin.common.ItemStackMixin} only attaches the interface). Ported call sites
 * {@code stack.getCapability(...)} become {@code stack.sophisticatedCore_getCapability(...)}.
 */
public interface SophisticatedItemStack {
	default <T, C extends @Nullable Object> @Nullable T sophisticatedCore_getCapability(ItemCapability<T, C> capability, C context) {
		return capability.getCapability((ItemStack) (Object) this, context);
	}

	default <T> @Nullable T sophisticatedCore_getCapability(ItemCapability<T, @Nullable Void> capability) {
		return capability.getCapability((ItemStack) (Object) this, null);
	}
}
