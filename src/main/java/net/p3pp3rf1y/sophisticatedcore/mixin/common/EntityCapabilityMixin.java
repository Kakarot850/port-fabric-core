package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.world.entity.Entity;
import net.p3pp3rf1y.sophisticatedcore.extensions.entity.SophisticatedEntity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Attaches {@link SophisticatedEntity} (NeoForge's capability query overloads, implemented as default methods) to
 * {@link Entity}.
 */
@Mixin(Entity.class)
public abstract class EntityCapabilityMixin implements SophisticatedEntity {
}
