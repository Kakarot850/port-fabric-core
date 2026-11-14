package net.p3pp3rf1y.sophisticatedcore.extensions.block.entity;

/**
 * Extension interface injected onto {@link net.minecraft.world.level.block.entity.BlockEntity} to provide
 * NeoForge's {@code onLoad} and {@code onChunkUnloaded} lifecycle hooks on Fabric.
 */
public interface SophisticatedBlockEntity {
	default void sophisticatedCore_onLoad() {
	}

	default void sophisticatedCore_onChunkUnloaded() {
	}
}
