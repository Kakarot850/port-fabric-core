package net.p3pp3rf1y.sophisticatedcore.init;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.crafting.ItemEnabledCondition;
import net.p3pp3rf1y.sophisticatedcore.fabric.registries.DeferredRegister;

public class ModRecipes {
	private ModRecipes() {
	}

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER,
			SophisticatedCore.MOD_ID);
	// NeoForge: CONDITION_CODECS (NeoForgeRegistries.Keys.CONDITION_CODECS). Fabric registers resource condition types instead,
	// see ITEM_ENABLED_CONDITION.

	// TODO(port) pending: UPGRADE_NEXT_TIER_SERIALIZER, UPGRADE_CLEAR_SERIALIZER, ENDER_LINKER_ENDPOINT_SERIALIZER,
	//  ENDER_LINKER_CLEAR_SERIALIZER. Their recipe classes are in the IStorageWrapper cycle (see PROGRESS.md).

	public static void registerHandlers() {
		RECIPE_SERIALIZERS.register();
		ResourceConditions.register(ITEM_ENABLED_CONDITION);
	}

	// NeoForge: Supplier<MapCodec<ItemEnabledCondition>> registered as "item_enabled" in CONDITION_CODECS.
	public static final ResourceConditionType<ItemEnabledCondition> ITEM_ENABLED_CONDITION = ResourceConditionType.create(
			SophisticatedCore.getIdentifier("item_enabled"), ItemEnabledCondition.CODEC);
}
