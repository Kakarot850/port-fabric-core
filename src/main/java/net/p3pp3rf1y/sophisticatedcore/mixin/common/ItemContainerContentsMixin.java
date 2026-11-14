package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.ItemContainerContents;
import net.p3pp3rf1y.sophisticatedcore.extensions.item.component.SophisticatedItemContainerContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Optional;

/**
 * Implements NeoForge 26.1.2's {@code ItemContainerContents} patch ({@code getSlots}, {@code getStackInSlot},
 * {@code validateSlotIndex}) for {@link SophisticatedItemContainerContents}.
 */
@Mixin(ItemContainerContents.class)
public class ItemContainerContentsMixin implements SophisticatedItemContainerContents {
	@Shadow
	@Final
	private List<Optional<ItemStackTemplate>> items;

	@Override
	public int sophisticatedCore_getSlots() {
		return items.size();
	}

	@Override
	public ItemStack sophisticatedCore_getStackInSlot(int slot) {
		sophisticatedCore_validateSlotIndex(slot);
		return items.get(slot).map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);
	}

	@Unique
	private void sophisticatedCore_validateSlotIndex(int slot) {
		if (slot < 0 || slot >= sophisticatedCore_getSlots()) {
			throw new UnsupportedOperationException("Slot " + slot + " not in valid range - [0," + sophisticatedCore_getSlots() + ")");
		}
	}
}
