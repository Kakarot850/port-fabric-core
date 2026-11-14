package net.p3pp3rf1y.sophisticatedcore.fabric.capabilities;

import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.capabilities.EntityCapability}: a capability queried for an
 * entity, e.g. {@link Capabilities.Item#ENTITY}.
 *
 * <p>Port: backed by a Fabric {@link EntityApiLookup} with the same id and classes. Fabric's Transfer API has no
 * entity lookups, so there is nothing to bridge; NeoForge's vanilla entity providers that core and storage rely on
 * are registered in {@link Capabilities}.
 */
public final class EntityCapability<T, C extends @Nullable Object> extends BaseCapability<T, C> {
	private final EntityApiLookup<T, C> lookup;

	private EntityCapability(Identifier name, Class<T> typeClass, Class<C> contextClass) {
		super(name, typeClass, contextClass);
		this.lookup = EntityApiLookup.get(name, typeClass, contextClass);
	}

	public static <T, C extends @Nullable Object> EntityCapability<T, C> create(Identifier name, Class<T> typeClass, Class<C> contextClass) {
		return new EntityCapability<>(name, typeClass, contextClass);
	}

	public static <T> EntityCapability<T, @Nullable Direction> createSided(Identifier name, Class<T> typeClass) {
		return create(name, typeClass, Direction.class);
	}

	public static <T> EntityCapability<T, @Nullable Void> createVoid(Identifier name, Class<T> typeClass) {
		return create(name, typeClass, Void.class);
	}

	public @Nullable T getCapability(Entity entity, C context) {
		return lookup.find(entity, context);
	}

	/**
	 * Port addition: the Fabric lookup backing this capability.
	 */
	public EntityApiLookup<T, C> lookup() {
		return lookup;
	}

	@SuppressWarnings("unchecked")
	<E extends Entity> void registerEntity(EntityType<E> type, ICapabilityProvider<? super E, C, T> provider) {
		lookup.registerForTypes((entity, context) -> provider.getCapability((E) entity, context), type);
	}
}
