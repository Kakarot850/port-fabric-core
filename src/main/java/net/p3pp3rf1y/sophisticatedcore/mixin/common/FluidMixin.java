package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.world.level.material.Fluid;
import net.p3pp3rf1y.sophisticatedcore.extensions.level.material.SophisticatedFluid;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Function;

/**
 * Implements NeoForge 26.1.2's {@code Fluid#computeDefaultResource} patch for {@link SophisticatedFluid}.
 */
@Mixin(Fluid.class)
public class FluidMixin implements SophisticatedFluid {
	@Unique
	private @Nullable FluidResource sophisticatedCore_defaultResource;

	@Override
	public FluidResource sophisticatedCore_computeDefaultResource(Function<Fluid, FluidResource> resourceConstructor) {
		if (sophisticatedCore_defaultResource == null) {
			sophisticatedCore_defaultResource = resourceConstructor.apply((Fluid) (Object) this);
		}
		return sophisticatedCore_defaultResource;
	}
}
