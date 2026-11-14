package net.p3pp3rf1y.sophisticatedcore.fabric.client.event;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.p3pp3rf1y.sophisticatedcore.fabric.network.payload.RecipeContentPayload;

import java.util.List;
import java.util.Set;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.client.event.RecipesReceivedEvent}: fired on the client when
 * the recipes requested through {@link net.p3pp3rf1y.sophisticatedcore.fabric.event.OnDatapackSyncEvent#sendRecipes}
 * arrive.
 *
 * <p>Port: NeoForge fires it with no recipes when connected to a server that cannot send them; here it is fired with
 * no recipes on disconnect instead, so recipes from a previous server never leak into a session that receives none.
 * Listen with {@code RecipesReceivedEvent.EVENT.register(...)}; {@link #registerClient()} installs the hooks.
 */
public class RecipesReceivedEvent {
	public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, listeners -> event -> {
		for (Listener listener : listeners) {
			listener.onRecipesReceived(event);
		}
	});

	private static boolean registered = false;

	private final Set<RecipeType<?>> recipeTypes;
	private final RecipeMap recipeMap;

	public RecipesReceivedEvent(Set<RecipeType<?>> recipeTypes, RecipeMap recipeMap) {
		this.recipeTypes = recipeTypes;
		this.recipeMap = recipeMap;
	}

	/**
	 * Port addition: registers the client payload receiver. Called once from the client entrypoint.
	 */
	public static synchronized void registerClient() {
		if (registered) {
			return;
		}
		registered = true;
		ClientPlayNetworking.registerGlobalReceiver(RecipeContentPayload.TYPE, (payload, context) ->
				EVENT.invoker().onRecipesReceived(new RecipesReceivedEvent(payload.recipeTypes(), RecipeMap.create(payload.recipes()))));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
				client.execute(() -> EVENT.invoker().onRecipesReceived(new RecipesReceivedEvent(Set.of(), RecipeMap.create(List.of())))));
	}

	/**
	 * {@return the recipe types the server sent} Recipes of other types are not in {@link #getRecipeMap()}.
	 */
	public Set<RecipeType<?>> getRecipeTypes() {
		return recipeTypes;
	}

	public RecipeMap getRecipeMap() {
		return recipeMap;
	}

	@FunctionalInterface
	public interface Listener {
		void onRecipesReceived(RecipesReceivedEvent event);
	}
}
