package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.server.ReloadableServerResources;
import net.p3pp3rf1y.sophisticatedcore.extensions.item.SophisticatedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge fires {@code DefaultDataComponentsBoundEvent(SERVER_DATA_LOAD)} at the end of
 * {@code updateComponentsAndStaticRegistryTags}, and resets every item's default transfer resource from it
 * ({@code NeoForgeMod}). Same reset here.
 */
@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
	@Inject(method = "updateComponentsAndStaticRegistryTags", at = @At("TAIL"))
	private void sophisticatedCore_resetDefaultResources(CallbackInfo ci) {
		SophisticatedItem.resetAllDefaultResources();
	}
}
