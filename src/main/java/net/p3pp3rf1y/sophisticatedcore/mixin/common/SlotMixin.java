package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.p3pp3rf1y.sophisticatedcore.extensions.inventory.SophisticatedSlot;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Implements NeoForge 26.1.2's {@code Slot} patch for {@link SophisticatedSlot}: {@code getSlotIndex},
 * {@code isSameInventory}, {@code setBackground}, and {@code getNoItemIcon} returning the background sprite instead of
 * {@code null}.
 */
@Mixin(Slot.class)
public class SlotMixin implements SophisticatedSlot {
	@Shadow
	@Final
	private int slot;

	@Shadow
	@Final
	public Container container;

	@Unique
	private @Nullable Identifier sophisticatedCore_backgroundSprite;

	@Override
	public int sophisticatedCore_getSlotIndex() {
		return slot;
	}

	@Override
	public boolean sophisticatedCore_isSameInventory(Slot other) {
		return container == other.container;
	}

	@Override
	public Slot sophisticatedCore_setBackground(Identifier sprite) {
		sophisticatedCore_backgroundSprite = sprite;
		return (Slot) (Object) this;
	}

	@Inject(method = "getNoItemIcon", at = @At("HEAD"), cancellable = true)
	private void sophisticatedCore_getNoItemIcon(CallbackInfoReturnable<@Nullable Identifier> cir) {
		cir.setReturnValue(sophisticatedCore_backgroundSprite);
	}
}
