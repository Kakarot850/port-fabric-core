package net.p3pp3rf1y.sophisticatedcore.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.item.crafting.RecipeType;
import net.p3pp3rf1y.sophisticatedcore.fabric.network.payload.RecipeContentPayload;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.event.OnDatapackSyncEvent}: fired when data pack contents are
 * synced to a player; listeners can request full recipes of some types to be sent ({@link #sendRecipes}).
 *
 * <p>Port: fired from Fabric's {@code ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS}, i.e. once per player both on
 * join and after {@code /reload} (NeoForge fires once with a {@code null} player after a reload). The requested
 * recipes go out in {@link RecipeContentPayload} to players whose client can receive it (i.e. has this mod).
 * Listen with {@code OnDatapackSyncEvent.EVENT.register(...)}; {@link #register()} installs the hooks.
 */
public class OnDatapackSyncEvent {
	public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, listeners -> event -> {
		for (Listener listener : listeners) {
			listener.onDatapackSync(event);
		}
	});

	private static boolean registered = false;

	private final PlayerList playerList;
	private final @Nullable ServerPlayer player;
	private final Set<RecipeType<?>> recipeTypesToSend = new HashSet<>();

	public OnDatapackSyncEvent(PlayerList playerList, @Nullable ServerPlayer player) {
		this.playerList = playerList;
		this.player = player;
	}

	/**
	 * Port addition: registers the payload type and the Fabric hook. Called once from {@code onInitialize}.
	 */
	public static synchronized void register() {
		if (registered) {
			return;
		}
		registered = true;
		PayloadTypeRegistry.clientboundPlay().registerLarge(RecipeContentPayload.TYPE, RecipeContentPayload.STREAM_CODEC, RecipeContentPayload.MAX_SIZE);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
			OnDatapackSyncEvent event = new OnDatapackSyncEvent(player.level().getServer().getPlayerList(), player);
			EVENT.invoker().onDatapackSync(event);
			if (!event.recipeTypesToSend.isEmpty() && ServerPlayNetworking.canSend(player, RecipeContentPayload.TYPE)) {
				ServerPlayNetworking.send(player, RecipeContentPayload.create(event.recipeTypesToSend,
						player.level().getServer().getRecipeManager().sophisticatedCore_recipeMap()));
			}
		});
	}

	public PlayerList getPlayerList() {
		return playerList;
	}

	/**
	 * {@return the player the data is synced to} Always set on Fabric (see the class javadoc).
	 */
	public @Nullable ServerPlayer getPlayer() {
		return player;
	}

	public Stream<ServerPlayer> getRelevantPlayers() {
		return player == null ? playerList.getPlayers().stream() : Stream.of(player);
	}

	/**
	 * Requests that all recipes of the given types are sent to the players.
	 */
	public void sendRecipes(RecipeType<?>... recipeTypes) {
		Collections.addAll(recipeTypesToSend, recipeTypes);
	}

	/**
	 * Requests that all recipes of the given types are sent to the players.
	 */
	public void sendRecipes(Iterable<RecipeType<?>> recipeTypes) {
		for (var recipeType : recipeTypes) {
			recipeTypesToSend.add(recipeType);
		}
	}

	public Set<RecipeType<?>> getRecipeTypesToSend() {
		return Collections.unmodifiableSet(recipeTypesToSend);
	}

	@FunctionalInterface
	public interface Listener {
		void onDatapackSync(OnDatapackSyncEvent event);
	}
}
