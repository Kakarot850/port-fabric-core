package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.p3pp3rf1y.sophisticatedcore.common.gui.SophisticatedMenuProvider;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * NeoForge's {@code ServerPlayer} patch: in {@code openMenu}, a provider whose
 * {@code shouldTriggerClientSideContainerClosingOnOpen()} is false gets {@code doCloseContainer()} (server-side close, no
 * close packet) instead of {@code closeContainer()}. On Fabric only {@link SophisticatedMenuProvider} exposes the flag.
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
	@Shadow
	public abstract void doCloseContainer();

	@WrapOperation(method = "openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;closeContainer()V", ordinal = 0))
	private void sophisticatedCore_closeContainerOnOpen(ServerPlayer player, Operation<Void> original, @Local(argsOnly = true) @Nullable MenuProvider provider) {
		if (provider instanceof SophisticatedMenuProvider menuProvider && !menuProvider.shouldTriggerClientSideContainerClosingOnOpen()) {
			doCloseContainer();
		} else {
			original.call(player);
		}
	}
}
