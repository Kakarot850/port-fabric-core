package net.p3pp3rf1y.sophisticatedcore.fabric.network.handling;

import net.minecraft.world.entity.player.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Fabric stand-in for NeoForge's {@code net.neoforged.neoforge.network.handling.IPayloadContext}, limited to what
 * Sophisticated Core and Storage use ({@code player()}, {@code enqueueWork}). Implementations wrap Fabric's
 * {@code ServerPlayNetworking.Context} / {@code ClientPlayNetworking.Context}; see {@code PayloadRegistrar}.
 * As in NeoForge (default {@code HandlerThread.MAIN}), handlers already run on the game thread.
 */
public interface IPayloadContext {
	/**
	 * The server player for serverbound payloads, the local player for clientbound ones.
	 */
	Player player();

	CompletableFuture<Void> enqueueWork(Runnable task);

	<T> CompletableFuture<T> enqueueWork(Supplier<T> task);
}
