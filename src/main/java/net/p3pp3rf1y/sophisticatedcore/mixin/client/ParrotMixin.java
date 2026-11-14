package net.p3pp3rf1y.sophisticatedcore.mixin.client;

import net.minecraft.world.entity.animal.parrot.Parrot;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.StorageSoundHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Parrot.class)
/**
 * Ported from ref-neoforge-core {@code mixin.MixinParrot} (unchanged apart from the name).
 */
public class ParrotMixin {
	@Unique
	private static final double sophisticatedcore$PARTY_RANGE = 3.46;

	@Shadow
	private boolean partyParrot;

	@Inject(method = "aiStep", at = @At("TAIL"))
	private void sophisticatedcore$setPartyNearStorageJukebox(CallbackInfo ci) {
		Parrot parrot = (Parrot) (Object) this;
		if (parrot.level().isClientSide() && StorageSoundHandler.isStorageSoundPlayingNear(parrot.position(), sophisticatedcore$PARTY_RANGE)) {
			partyParrot = true;
		}
	}
}
