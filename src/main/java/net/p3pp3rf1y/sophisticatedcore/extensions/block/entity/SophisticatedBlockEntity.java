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

	default boolean sophisticatedCore_isLoaded() {
		return false;
	}

	default void sophisticatedCore_setLoaded(boolean loaded) {
	}

	default void sophisticatedCore_safeLoad() {
		if (!sophisticatedCore_isLoaded()) {
			sophisticatedCore_setLoaded(true);
			sophisticatedCore_onLoad();
		}
	}

	default void sophisticatedCore_safeUnload() {
		if (sophisticatedCore_isLoaded()) {
			sophisticatedCore_setLoaded(false);
			sophisticatedCore_onChunkUnloaded();
		}
	}
}
