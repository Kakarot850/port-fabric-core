package net.p3pp3rf1y.sophisticatedcore.init;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderData;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.CookingLogic;

import java.util.Map;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.Recipe;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.common.gui.SortBy;
import net.p3pp3rf1y.sophisticatedcore.fabric.fluids.SimpleFluidContent;
import net.p3pp3rf1y.sophisticatedcore.linkedstorage.EnderLinkPendingCraftData;
import net.p3pp3rf1y.sophisticatedcore.linkedstorage.EnderLinkerTargetData;
import net.p3pp3rf1y.sophisticatedcore.linkedstorage.LinkedStorageEndpointData;
import net.p3pp3rf1y.sophisticatedcore.upgrades.EntityMatch;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterAttributes;
import net.p3pp3rf1y.sophisticatedcore.upgrades.alchemy.AlchemyFilterAttribute;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.HungerLevel;
import net.p3pp3rf1y.sophisticatedcore.upgrades.filter.Direction;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.RepeatMode;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.AutomationDirection;
import net.p3pp3rf1y.sophisticatedcore.util.CodecHelper;
import net.p3pp3rf1y.sophisticatedcore.util.SimpleItemContent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class ModCoreDataComponents {
	// Fabric: plain DataComponentType values registered when this class initializes (see PROGRESS.md "Port Conventions").
	public static final DataComponentType<Integer> NUMBER_OF_INVENTORY_SLOTS = register("number_of_inventory_slots",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
	public static final DataComponentType<Integer> NUMBER_OF_UPGRADE_SLOTS = register("number_of_upgrade_slots",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final DataComponentType<Integer> MAIN_COLOR = register("main_color",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
	public static final DataComponentType<Integer> ACCENT_COLOR = register("accent_color",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final DataComponentType<UUID> STORAGE_UUID = register("storage_uuid",
			() -> new DataComponentType.Builder<UUID>().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());

	public static final DataComponentType<LinkedStorageEndpointData> LINKED_STORAGE_ENDPOINT = register("linked_storage_endpoint", () -> new DataComponentType.Builder<LinkedStorageEndpointData>().persistent(LinkedStorageEndpointData.CODEC)
					.networkSynchronized(LinkedStorageEndpointData.STREAM_CODEC).build());
	public static final DataComponentType<Boolean> LINKED_STORAGE_PRIMARY_ENDPOINT = register("linked_storage_primary_endpoint",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
	public static final DataComponentType<Long> LINKED_STORAGE_RENDER_REVISION = register("linked_storage_render_revision",
			() -> new DataComponentType.Builder<Long>().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());
	public static final DataComponentType<EnderLinkerTargetData> ENDER_LINKER_TARGET = register("ender_linker_target",
			() -> new DataComponentType.Builder<EnderLinkerTargetData>().persistent(EnderLinkerTargetData.CODEC)
					.networkSynchronized(EnderLinkerTargetData.STREAM_CODEC).build());
	public static final DataComponentType<EnderLinkPendingCraftData> ENDER_LINK_PENDING_CRAFT = register("ender_link_pending_craft", () -> new DataComponentType.Builder<EnderLinkPendingCraftData>().persistent(EnderLinkPendingCraftData.CODEC)
					.networkSynchronized(EnderLinkPendingCraftData.STREAM_CODEC).build());

	public static final DataComponentType<Integer> OPEN_TAB_ID = register("open_tab_id",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final DataComponentType<SortBy> SORT_BY = register("sort_by",
			() -> new DataComponentType.Builder<SortBy>().persistent(SortBy.CODEC).networkSynchronized(SortBy.STREAM_CODEC).build());

	public static final DataComponentType<RenderData> RENDER_DATA = register("render_data",
			() -> new DataComponentType.Builder<RenderData>().persistent(RenderData.CODEC).networkSynchronized(RenderData.STREAM_CODEC).build());

	public static final DataComponentType<Boolean> SHIFT_CLICK_INTO_STORAGE = register("shift_click_into_storage",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<Boolean> REFILL_CRAFTING_GRID = register("refill_crafting_grid",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<Boolean> REFILL_INPUT = register("refill_input",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<SimpleItemContent> INPUT_ITEM = register("input_item",
			() -> new DataComponentType.Builder<SimpleItemContent>().persistent(SimpleItemContent.CODEC).networkSynchronized(SimpleItemContent.STREAM_CODEC)
					.build());

	public static final DataComponentType<SimpleItemContent> RESULT_ITEM = register("result_item",
			() -> new DataComponentType.Builder<SimpleItemContent>().persistent(SimpleItemContent.CODEC).networkSynchronized(SimpleItemContent.STREAM_CODEC)
					.build());

	public static final DataComponentType<Integer> ENERGY_STORED = register("energy_stored",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final DataComponentType<Boolean> COMPACT_NON_UNCRAFTABLE = register("compact_non_uncraftable",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<Boolean> SHOULD_WORK_IN_GUI = register("should_work_in_gui",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<ItemContainerContents> COOKING_INVENTORY = register("cooking_inventory",
			() -> new DataComponentType.Builder<ItemContainerContents>().persistent(CodecHelper.LENIENT_ITEM_CONTAINER_CONTENTS_CODEC)
					.networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding().build());

	public static final DataComponentType<Long> BURN_TIME_FINISH = register("burn_time_finish",
			() -> new DataComponentType.Builder<Long>().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());

	public static final DataComponentType<Integer> BURN_TIME_TOTAL = register("burn_time_total",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final DataComponentType<Long> COOK_TIME_FINISH = register("cook_time_finish",
			() -> new DataComponentType.Builder<Long>().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());

	public static final DataComponentType<Integer> COOK_TIME_TOTAL = register("cook_time_total",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final DataComponentType<Boolean> IS_COOKING = register("is_cooking",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<HungerLevel> FEED_AT_HUNGER_LEVEL = register("feed_at_hunger_level",
			() -> new DataComponentType.Builder<HungerLevel>().persistent(HungerLevel.CODEC).networkSynchronized(HungerLevel.STREAM_CODEC).build());

	public static final DataComponentType<Boolean> FEED_IMMEDIATELY_WHEN_HURT = register("feed_immediately_when_hurt",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<Direction> DIRECTION = register("direction",
			() -> new DataComponentType.Builder<Direction>().persistent(Direction.CODEC).networkSynchronized(Direction.STREAM_CODEC).build());

	public static final DataComponentType<Boolean> IS_PLAYING = register("is_playing",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<Boolean> PICKUP_ITEMS = register("pickup_items",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<Boolean> PICKUP_XP = register("pickup_xp",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<List<SimpleFluidContent>> FLUID_FILTERS = register("fluid_filters",
			() -> new DataComponentType.Builder<List<SimpleFluidContent>>().persistent(Codec.list(SimpleFluidContent.CODEC))
					.networkSynchronized(SimpleFluidContent.STREAM_CODEC.apply(ByteBufCodecs.list())).build());

	public static final DataComponentType<Boolean> IS_INPUT = register("is_input",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<Boolean> INTERACT_WITH_HAND = register("interact_with_hand",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<Boolean> INTERACT_WITH_WORLD = register("interact_with_world",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<Boolean> INTERACT_WITH_FLUID_HANDLERS = register("interact_with_fluid_handler",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<ResourceKey<Recipe<?>>> RECIPE_ID = register("recipe_id",
			() -> new DataComponentType.Builder<ResourceKey<Recipe<?>>>().persistent(ResourceKey.codec(Registries.RECIPE))
					.networkSynchronized(ResourceKey.streamCodec(Registries.RECIPE)).build());

	public static final DataComponentType<SimpleFluidContent> FLUID_CONTENTS = register("fluid_contents",
			() -> new DataComponentType.Builder<SimpleFluidContent>().persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC)
					.build());

	@Deprecated
	public static final DataComponentType<Boolean> LEGACY_SHOULD_VOID_OVERFLOW = register("should_void_overflow",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<VoidType> VOID_TYPE = register("void_type",
			() -> new DataComponentType.Builder<VoidType>().persistent(VoidType.CODEC).networkSynchronized(VoidType.STREAM_CODEC).build());

	public static final DataComponentType<AutomationDirection> AUTOMATION_DIRECTION = register("automation_direction",
			() -> new DataComponentType.Builder<AutomationDirection>().persistent(AutomationDirection.CODEC)
					.networkSynchronized(AutomationDirection.STREAM_CODEC).build());

	public static final DataComponentType<Integer> LEVEL = register("level",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final DataComponentType<Integer> LEVELS_TO_STORE = register("levels_to_store",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final DataComponentType<Integer> LEVELS_TO_TAKE = register("levels_to_take",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final DataComponentType<Boolean> MEND_ITEMS = register("mend_items",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<FilterAttributes> FILTER_ATTRIBUTES = register("filter_attributes", () -> new DataComponentType.Builder<FilterAttributes>().persistent(FilterAttributes.CODEC)
					.networkSynchronized(FilterAttributes.STREAM_CODEC).build());

	public static final DataComponentType<FilterAttributes> INPUT_FILTER_ATTRIBUTES = register("input_filter_attributes", () -> new DataComponentType.Builder<FilterAttributes>().persistent(FilterAttributes.CODEC)
					.networkSynchronized(FilterAttributes.STREAM_CODEC).build());

	public static final DataComponentType<FilterAttributes> FUEL_FILTER_ATTRIBUTES = register("fuel_filter_attributes", () -> new DataComponentType.Builder<FilterAttributes>().persistent(FilterAttributes.CODEC)
					.networkSynchronized(FilterAttributes.STREAM_CODEC).build());

	public static final DataComponentType<List<AlchemyFilterAttribute>> ALCHEMY_FILTER_ATTRIBUTES = register(
			"alchemy_filter_attributes",
			() -> new DataComponentType.Builder<List<AlchemyFilterAttribute>>().persistent(Codec.list(AlchemyFilterAttribute.CODEC))
					.networkSynchronized(AlchemyFilterAttribute.STREAM_CODEC.apply(ByteBufCodecs.list())).build());

	public static final DataComponentType<Boolean> ENABLED = register("enabled",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<RepeatMode> REPEAT_MODE = register("repeat_mode",
			() -> new DataComponentType.Builder<RepeatMode>().persistent(RepeatMode.CODEC).networkSynchronized(RepeatMode.STREAM_CODEC).build());

	public static final DataComponentType<Boolean> SHUFFLE = register("shuffle",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<Integer> DISC_SLOT_ACTIVE = register("disc_slot_active",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final DataComponentType<Long> DISC_FINISH_TIME = register("disc_finish_time",
			() -> new DataComponentType.Builder<Long>().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());

	public static final DataComponentType<Boolean> MATCH_ALL_EFFECTS = register("match_all_effects",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<Boolean> MATCH_EFFECT_DURATION = register("match_effect_duration",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<Boolean> MATCH_EFFECT_AMPLIFIER = register("match_effect_amplifier",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final DataComponentType<EntityMatch> ENTITY_MATCH = register("entity_match",
			() -> new DataComponentType.Builder<EntityMatch>().persistent(EntityMatch.CODEC).networkSynchronized(EntityMatch.STREAM_CODEC).build());

	public static final DataComponentType<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED = register("recipes_used",
			() -> new DataComponentType.Builder<Map<ResourceKey<Recipe<?>>, Integer>>()
					.persistent(CookingLogic.RECIPES_USED_CODEC).networkSynchronized(CookingLogic.RECIPES_USED_STREAM_CODEC).build());

	public static final DataComponentType<Float> STORED_XP = register("stored_xp",
			() -> new DataComponentType.Builder<Float>().persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT).build());

	public static final DataComponentType<ItemContainerContents> LENIENT_CONTAINER = register("lenient_container",
			() -> new DataComponentType.Builder<ItemContainerContents>().persistent(CodecHelper.LENIENT_ITEM_CONTAINER_CONTENTS_CODEC)
					.networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding().build());

	private static <T> DataComponentType<T> register(String name, Supplier<DataComponentType<T>> factory) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, SophisticatedCore.getIdentifier(name), factory.get());
	}

	/**
	 * Fabric: every component is registered while this class initializes; calling this from {@code onInitialize} makes
	 * that happen before the registries freeze.
	 */
	public static void register() {
		BuiltInRegistries.DATA_COMPONENT_TYPE.addAlias(SophisticatedCore.getIdentifier("render_info_tag"), SophisticatedCore.getIdentifier("render_data"));
	}
}
