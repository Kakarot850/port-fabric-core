package net.p3pp3rf1y.sophisticatedcore.extensions.level.material;

import net.minecraft.world.level.material.Fluid;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.fluid.FluidResource;

import java.util.function.Function;

/**
 * NeoForge's {@code Fluid} patch caching the component-less {@link FluidResource} per fluid
 * ({@code computeDefaultResource}), added through Loom interface injection and implemented by
 * {@code mixin.common.FluidMixin}. Internal to the transfer stand-ins: use {@link FluidResource#of}.
 */
public interface SophisticatedFluid {
	/**
	 * {@return the cached default resource of this fluid, created with {@code resourceConstructor} on first use}
	 */
	default FluidResource sophisticatedCore_computeDefaultResource(Function<Fluid, FluidResource> resourceConstructor) {
		throw new UnsupportedOperationException("Implemented by FluidMixin");
	}
}
