package net.p3pp3rf1y.sophisticatedcore.fabric.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedcore.fabric.network.handling.IPayloadContext;
import net.p3pp3rf1y.sophisticatedcore.fabric.network.registration.PayloadRegistrar;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Client half of {@link PayloadRegistrar}: registers a mod's clientbound handlers with {@code ClientPlayNetworking}.
 * Call {@link #register(String)} from the mod's {@code ClientModInitializer}.
 */
public final class ClientPayloadHandlers {
	private ClientPayloadHandlers() {
	}

	public static void register(String modId) {
		for (PayloadRegistrar.ClientHandler<?> handler : PayloadRegistrar.takeClientHandlers(modId)) {
			register(handler);
		}
	}

	private static <T extends CustomPacketPayload> void register(PayloadRegistrar.ClientHandler<T> handler) {
		ClientPlayNetworking.registerGlobalReceiver(handler.type(), (payload, context) -> handler.handler().handle(payload, new ClientContext(context)));
	}

	private record ClientContext(ClientPlayNetworking.Context context) implements IPayloadContext {
		@Override
		public Player player() {
			return context.player();
		}

		@Override
		public CompletableFuture<Void> enqueueWork(Runnable task) {
			return context.client().submit(task);
		}

		@Override
		public <T> CompletableFuture<T> enqueueWork(Supplier<T> task) {
			return context.client().submit(task);
		}
	}
}
