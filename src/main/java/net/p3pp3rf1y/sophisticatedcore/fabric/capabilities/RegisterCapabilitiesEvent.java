package net.p3pp3rf1y.sophisticatedcore.fabric.capabilities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent}: registers
 * capability providers.
 *
 * <p>Port: not an event. Registrations go straight into the Fabric lookups, so create an instance and pass it to the
 * mod's registration method from {@code onInitialize}, after the block entity / entity types are registered
 * (NeoForge fires the event after registries freeze). Bridged block capabilities also expose block entity
 * registrations to Fabric's Transfer API (see {@link BlockCapability}).
 */
public class RegisterCapabilitiesEvent {
	public <T, C extends @Nullable Object, E extends BlockEntity> void registerBlockEntity(BlockCapability<T, C> capability, BlockEntityType<E> blockEntityType,
			ICapabilityProvider<? super E, C, T> provider) {
		capability.registerBlockEntity(blockEntityType, provider);
	}

	public <T, C extends @Nullable Object> void registerBlock(BlockCapability<T, C> capability, IBlockCapabilityProvider<T, C> provider, Block... blocks) {
		capability.lookup().registerForBlocks(provider::getCapability, blocks);
	}

	public <T, C extends @Nullable Object, E extends Entity> void registerEntity(EntityCapability<T, C> capability, EntityType<E> entityType,
			ICapabilityProvider<? super E, C, T> provider) {
		capability.registerEntity(entityType, provider);
	}

	public <T, C extends @Nullable Object> void registerItem(ItemCapability<T, C> capability, ICapabilityProvider<ItemStack, C, T> provider, ItemLike... items) {
		capability.registerItems(provider, items);
	}
}
