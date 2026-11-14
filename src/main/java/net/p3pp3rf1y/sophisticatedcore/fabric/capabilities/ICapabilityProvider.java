package net.p3pp3rf1y.sophisticatedcore.fabric.capabilities;

import org.jspecify.annotations.Nullable;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.capabilities.ICapabilityProvider}: computes a capability for an
 * object (block entity, entity or item stack) and context.
 *
 * @param <O> type of the object the capability is computed for
 * @param <C> type of the context
 * @param <T> type of the capability
 */
@FunctionalInterface
public interface ICapabilityProvider<O, C extends @Nullable Object, T> {
	@Nullable
	T getCapability(O object, C context);
}
