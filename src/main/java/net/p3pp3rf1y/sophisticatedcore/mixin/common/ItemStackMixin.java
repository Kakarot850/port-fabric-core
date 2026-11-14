package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.extensions.item.SophisticatedItemStack;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Attaches {@link SophisticatedItemStack} (NeoForge's capability query overloads, implemented as default methods) to
 * {@link ItemStack}.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements SophisticatedItemStack {
}
