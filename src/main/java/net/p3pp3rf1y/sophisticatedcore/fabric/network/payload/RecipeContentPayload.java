package net.p3pp3rf1y.sophisticatedcore.fabric.network.payload;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stand-in for NeoForge's internal {@code net.neoforged.neoforge.network.payload.RecipeContentPayload}: the full
 * recipes of the requested types, sent to clients (vanilla 26.1 only sends recipe displays). Same codec; the id uses
 * the {@code sophisticatedcore} namespace. Sent by {@link net.p3pp3rf1y.sophisticatedcore.fabric.event.OnDatapackSyncEvent},
 * received by {@link net.p3pp3rf1y.sophisticatedcore.fabric.client.event.RecipesReceivedEvent}.
 */
public record RecipeContentPayload(Set<RecipeType<?>> recipeTypes, List<RecipeHolder<?>> recipes) implements CustomPacketPayload {
	public static final Type<RecipeContentPayload> TYPE = new Type<>(SophisticatedCore.getIdentifier("recipe_content"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RecipeContentPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.registry(Registries.RECIPE_TYPE).apply(ByteBufCodecs.collection(HashSet::new)), RecipeContentPayload::recipeTypes,
			RecipeHolder.STREAM_CODEC.apply(ByteBufCodecs.list()), RecipeContentPayload::recipes,
			RecipeContentPayload::new);
	/**
	 * Same limit as Fabric's own recipe sync payload: recipe data can be large in big packs.
	 */
	public static final int MAX_SIZE = 64 * 1024 * 1024;

	public static RecipeContentPayload create(Collection<RecipeType<?>> recipeTypes, RecipeMap recipes) {
		var recipeTypeSet = Set.copyOf(recipeTypes);
		if (recipeTypeSet.isEmpty()) {
			return new RecipeContentPayload(recipeTypeSet, List.of());
		}
		var recipeSubset = recipes.values().stream().filter(h -> recipeTypeSet.contains(h.value().getType())).toList();
		return new RecipeContentPayload(recipeTypeSet, recipeSubset);
	}

	@Override
	public Type<RecipeContentPayload> type() {
		return TYPE;
	}
}
