package net.p3pp3rf1y.sophisticatedcore.extensions.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.FuelValues;
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

	/**
	 * NeoForge {@code IItemStackExtension#getBurnTime}: 0 for empty stacks, else the item's burn time, which defaults to
	 * vanilla's fuel values (NeoForge's per-item override and burn-time event don't exist on Fabric; Fabric's
	 * {@code FuelRegistryEvents} feed {@link FuelValues} directly).
	 */
	default int sophisticatedCore_getBurnTime(@Nullable RecipeType<?> recipeType, FuelValues fuelValues) {
		ItemStack self = (ItemStack) (Object) this;
		return self.isEmpty() ? 0 : fuelValues.burnDuration(self);
	}

	/**
	 * NeoForge {@code IItemStackExtension#getXpRepairRatio}: durability repaired per experience point, 1 by default
	 * ({@code IItemExtension#getXpRepairRatio}); there is no per-item override on Fabric.
	 */
	default float sophisticatedCore_getXpRepairRatio() {
		return 1f;
	}
}
