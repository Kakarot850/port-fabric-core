package net.p3pp3rf1y.sophisticatedcore.fabric.capabilities;

import net.minecraft.resources.Identifier;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.capabilities.BaseCapability}: the name, type and context
 * class shared by block, entity and item capabilities.
 *
 * @param <T> type of the queried object
 * @param <C> type of the additional context
 */
public abstract class BaseCapability<T, C> {
	private final Identifier name;
	private final Class<T> typeClass;
	private final Class<C> contextClass;

	protected BaseCapability(Identifier name, Class<T> typeClass, Class<C> contextClass) {
		this.name = name;
		this.typeClass = typeClass;
		this.contextClass = contextClass;
	}

	public final Identifier name() {
		return name;
	}

	public final Class<T> typeClass() {
		return typeClass;
	}

	public final Class<C> contextClass() {
		return contextClass;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + name + "]";
	}
}
