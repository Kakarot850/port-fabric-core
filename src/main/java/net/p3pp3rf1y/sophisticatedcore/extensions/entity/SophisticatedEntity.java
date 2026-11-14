package net.p3pp3rf1y.sophisticatedcore.extensions.entity;

import net.minecraft.nbt.CompoundTag;

import net.minecraft.world.entity.Entity;
import net.p3pp3rf1y.sophisticatedcore.fabric.capabilities.EntityCapability;
import org.jspecify.annotations.Nullable;

/**
 * NeoForge's {@code IEntityExtension#getCapability} overloads, added to {@link Entity} through Loom interface injection
 * ({@code mixin.common.EntityCapabilityMixin} only attaches the interface). Ported call sites
 * {@code entity.getCapability(...)} become {@code entity.sophisticatedCore_getCapability(...)}.
 */
public interface SophisticatedEntity {
	default <T, C extends @Nullable Object> @Nullable T sophisticatedCore_getCapability(EntityCapability<T, C> capability, C context) {
		return capability.getCapability((Entity) this, context);
	}

	default <T> @Nullable T sophisticatedCore_getCapability(EntityCapability<T, @Nullable Void> capability) {
		return capability.getCapability((Entity) this, null);
	}

	default CompoundTag sophisticatedCore_getCustomData() {
		return new CompoundTag();
	}
}
