package net.p3pp3rf1y.sophisticatedcore.fabric.capabilities;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.ResourceHandler;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.access.ItemAccess;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.bridge.ItemAccessContainerItemContext;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.bridge.ResourceHandlerStorage;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.bridge.StorageResourceHandler;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.energy.EnergyHandler;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.fluid.FluidResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.PlayerInventoryWrapper;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.VanillaContainerWrapper;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.capabilities.Capabilities}: the same constants.
 *
 * <p>Port: ids use the {@code sophisticatedcore} namespace (NeoForge: {@code neoforge}). Interop with Fabric's
 * Transfer API, set up by {@link #register()}:
 * <ul>
 * <li>{@link Item#BLOCK} / {@link Fluid#BLOCK} are bridged to {@code ItemStorage.SIDED} / {@code FluidStorage.SIDED}:
 * blocks without a registration here are answered by Fabric (which covers vanilla containers, composter, cauldrons
 * and other mods' blocks), and block entity registrations here are exposed to Fabric.</li>
 * <li>{@link Item#ITEM} / {@link Fluid#ITEM} fall back to {@code ItemStorage.ITEM} / {@code FluidStorage.ITEM} (Fabric
 * covers shulker boxes, buckets incl. modded {@code BucketItem}s, bottles), queried with the item access as
 * {@code ContainerItemContext}. NeoForge's own vanilla item providers are therefore not ported.</li>
 * <li>Fabric has no entity lookups: NeoForge's vanilla providers for {@link Item#ENTITY} (players, container
 * entities) and {@link Item#ENTITY_AUTOMATION} (container entities) are registered here. NeoForge's
 * catch-all living-entity/horse equipment provider is not ported (neither mod queries non-player entities).</li>
 * <li>Fabric API has no energy API: {@link Energy} capabilities only see providers registered here.</li>
 * </ul>
 */
public final class Capabilities {
	public static final class Energy {
		public static final BlockCapability<EnergyHandler, @Nullable Direction> BLOCK = BlockCapability.createSided(create("energy_handler"), EnergyHandler.class);
		public static final EntityCapability<EnergyHandler, @Nullable Direction> ENTITY = EntityCapability.createSided(create("energy_handler"), EnergyHandler.class);
		public static final ItemCapability<EnergyHandler, ItemAccess> ITEM = ItemCapability.create(create("energy_handler"), EnergyHandler.class, ItemAccess.class);

		private Energy() {}
	}

	public static final class Fluid {
		public static final BlockCapability<ResourceHandler<FluidResource>, @Nullable Direction> BLOCK = BlockCapability.createSided(create("fluid_handler"), ResourceHandler.asClass());
		public static final EntityCapability<ResourceHandler<FluidResource>, @Nullable Direction> ENTITY = EntityCapability.createSided(create("fluid_handler"), ResourceHandler.asClass());
		public static final ItemCapability<ResourceHandler<FluidResource>, ItemAccess> ITEM = ItemCapability.create(create("fluid_handler"), ResourceHandler.asClass(), ItemAccess.class);

		private Fluid() {}
	}

	public static final class Item {
		public static final BlockCapability<ResourceHandler<ItemResource>, @Nullable Direction> BLOCK = BlockCapability.createSided(create("item_handler"), ResourceHandler.asClass());
		public static final EntityCapability<ResourceHandler<ItemResource>, @Nullable Void> ENTITY = EntityCapability.createVoid(create("item_handler"), ResourceHandler.asClass());
		public static final EntityCapability<ResourceHandler<ItemResource>, @Nullable Direction> ENTITY_AUTOMATION = EntityCapability.createSided(create("item_handler_automation"), ResourceHandler.asClass());
		public static final ItemCapability<ResourceHandler<ItemResource>, ItemAccess> ITEM = ItemCapability.create(create("item_handler"), ResourceHandler.asClass(), ItemAccess.class);

		private Item() {}
	}

	private static boolean registered = false;

	private static Identifier create(String path) {
		return SophisticatedCore.getIdentifier(path);
	}

	private Capabilities() {}

	/**
	 * Port addition: sets up the Fabric Transfer API bridges and NeoForge's vanilla providers. Called once from
	 * {@code SophisticatedCore#onInitialize}, before any mod registers capabilities.
	 */
	public static synchronized void register() {
		if (registered) {
			return;
		}
		registered = true;

		Item.BLOCK.bridge(ItemStorage.SIDED, ResourceHandlerStorage::ofItems, StorageResourceHandler::ofItems);
		Fluid.BLOCK.bridge(FluidStorage.SIDED, ResourceHandlerStorage::ofFluids, StorageResourceHandler::ofFluids);

		Item.ITEM.fallback((stack, access) -> {
			Storage<ItemVariant> storage = ItemStorage.ITEM.find(stack, new ItemAccessContainerItemContext(access));
			return storage == null ? null : StorageResourceHandler.ofItems(storage);
		});
		Fluid.ITEM.fallback((stack, access) -> {
			Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, new ItemAccessContainerItemContext(access));
			return storage == null ? null : StorageResourceHandler.ofFluids(storage);
		});

		// NeoForge CapabilityHooks#registerVanillaProviders, entity part
		RegisterCapabilitiesEvent event = new RegisterCapabilitiesEvent();
		var containerEntities = List.of(
				EntityTypes.ACACIA_CHEST_BOAT,
				EntityTypes.BIRCH_CHEST_BOAT,
				EntityTypes.CHERRY_CHEST_BOAT,
				EntityTypes.DARK_OAK_CHEST_BOAT,
				EntityTypes.JUNGLE_CHEST_BOAT,
				EntityTypes.MANGROVE_CHEST_BOAT,
				EntityTypes.OAK_CHEST_BOAT,
				EntityTypes.SPRUCE_CHEST_BOAT,
				EntityTypes.BAMBOO_CHEST_RAFT,
				EntityTypes.PALE_OAK_CHEST_BOAT,
				EntityTypes.CHEST_MINECART,
				EntityTypes.HOPPER_MINECART);
		for (var entityType : containerEntities) {
			event.registerEntity(Item.ENTITY, entityType, (entity, ctx) -> VanillaContainerWrapper.of(entity));
			event.registerEntity(Item.ENTITY_AUTOMATION, entityType, (entity, ctx) -> VanillaContainerWrapper.of(entity));
		}
		event.registerEntity(Item.ENTITY, EntityTypes.PLAYER, (player, ctx) -> PlayerInventoryWrapper.of(player.getInventory()));
	}
}
