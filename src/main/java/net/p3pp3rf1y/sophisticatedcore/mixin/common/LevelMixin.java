package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedcore.extensions.level.SophisticatedLevel;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Attaches {@link SophisticatedLevel} (NeoForge's capability query overloads, implemented as default methods) to
 * {@link Level}.
 */
@Mixin(Level.class)
public abstract class LevelMixin implements SophisticatedLevel {
}
