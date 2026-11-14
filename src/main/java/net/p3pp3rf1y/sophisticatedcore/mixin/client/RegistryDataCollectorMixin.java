package net.p3pp3rf1y.sophisticatedcore.mixin.client;

import net.minecraft.client.multiplayer.RegistryDataCollector;
import net.minecraft.core.RegistryAccess;
import net.p3pp3rf1y.sophisticatedcore.extensions.item.SophisticatedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge fires {@code DefaultDataComponentsBoundEvent(CLIENT_PACKET_RECEIVED)} at the end of
 * {@code updateComponents}; its default-resource reset only runs when {@code shouldUpdateStaticData()}, i.e. when the
 * connection is not to the integrated server ({@code includeSharedRegistries}), because in singleplayer the server
 * thread already updated the shared static data.
 */
@Mixin(RegistryDataCollector.class)
public class RegistryDataCollectorMixin {
	@Inject(method = "updateComponents", at = @At("TAIL"))
	private static void sophisticatedCore_resetDefaultResources(RegistryAccess.Frozen frozenRegistries, boolean includeSharedRegistries, CallbackInfo ci) {
		if (includeSharedRegistries) {
			SophisticatedItem.resetAllDefaultResources();
		}
	}
}
