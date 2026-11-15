package net.p3pp3rf1y.sophisticatedcore.fabric.network.registration;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedcore.fabric.network.handling.IPayloadContext;
import net.p3pp3rf1y.sophisticatedcore.fabric.network.handling.IPayloadHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Fabric stand-in for NeoForge's {@code PayloadRegistrar} (play phase only, the parts Sophisticated mods use).
 * Call from {@code ModInitializer#onInitialize} on both physical sides:
 * <ul>
 * <li>{@link #playToServer}: registers the codec with {@code PayloadTypeRegistry.serverboundPlay()} and the handler with
 * {@code ServerPlayNetworking}.</li>
 * <li>{@link #playToClient}: registers the codec with {@code PayloadTypeRegistry.clientboundPlay()} and keeps the handler;
 * the mod's client entrypoint hands it to {@code ClientPlayNetworking} through
 * {@code fabric.client.network.ClientPayloadHandlers#register(modId)} (Fabric runs every main entrypoint first).</li>
 * </ul>
 * {@link #versioned} is kept for source compatibility only: Fabric has no per-namespace protocol-version negotiation.
 */
public class PayloadRegistrar {
	private static final List<ClientHandler<?>> PENDING_CLIENT_HANDLERS = new ArrayList<>();

	private final String modId;
	private final String version;

	public PayloadRegistrar(String modId) {
		this(modId, "1");
	}

	private PayloadRegistrar(String modId, String version) {
		this.modId = Objects.requireNonNull(modId);
		this.version = Objects.requireNonNull(version);
	}

	public PayloadRegistrar versioned(String version) {
		return new PayloadRegistrar(modId, version);
	}

	public PayloadRegistrar optional() {
		return this;
	}

	public String getVersion() {
		return version;
	}

	public <T extends CustomPacketPayload> PayloadRegistrar playToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, IPayloadHandler<T> handler) {
		PayloadTypeRegistry.clientboundPlay().register(type, codec);
		synchronized (PENDING_CLIENT_HANDLERS) {
			PENDING_CLIENT_HANDLERS.add(new ClientHandler<>(modId, type, handler));
		}
		return this;
	}

	public <T extends CustomPacketPayload> PayloadRegistrar playToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, IPayloadHandler<T> handler) {
		PayloadTypeRegistry.serverboundPlay().register(type, codec);
		ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.handle(payload, new ServerContext(context)));
		return this;
	}

	/**
	 * Removes and returns the clientbound handlers registered by {@code modId}. Used by the client-side registration.
	 */
	public static List<ClientHandler<?>> takeClientHandlers(String modId) {
		synchronized (PENDING_CLIENT_HANDLERS) {
			List<ClientHandler<?>> taken = new ArrayList<>();
			PENDING_CLIENT_HANDLERS.removeIf(h -> {
				if (h.modId().equals(modId)) {
					taken.add(h);
					return true;
				}
				return false;
			});
			return taken;
		}
	}

	public record ClientHandler<T extends CustomPacketPayload>(String modId, CustomPacketPayload.Type<T> type, IPayloadHandler<T> handler) {
	}

	private record ServerContext(ServerPlayNetworking.Context context) implements IPayloadContext {
		@Override
		public Player player() {
			return context.player();
		}

		@Override
		public CompletableFuture<Void> enqueueWork(Runnable task) {
			return context.server().submit(task);
		}

		@Override
		public <T> CompletableFuture<T> enqueueWork(Supplier<T> task) {
			return context.server().submit(task);
		}
	}
}
