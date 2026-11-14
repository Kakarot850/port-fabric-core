package net.p3pp3rf1y.sophisticatedcore.fabric.capabilities;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.capabilities.BlockCapability}: a capability queried for a
 * block position, e.g. {@link Capabilities.Item#BLOCK}.
 *
 * <p>Port: backed by a Fabric {@link BlockApiLookup} with the same id, classes and provider precedence (block entity /
 * block registrations first, then fallbacks). A capability can be {@linkplain #bridge bridged} to an equivalent
 * Fabric lookup: Fabric's providers then answer queries for blocks without a registration here, and every block
 * entity registration here is also exposed through the Fabric lookup, so other Fabric mods see our blocks.
 * Unlike NeoForge there is no per-level result invalidation: Fabric lookups call the provider on every query.
 *
 * @param <T> type of the queried object
 * @param <C> type of the context, usually {@code @Nullable Direction}
 */
public final class BlockCapability<T, C extends @Nullable Object> extends BaseCapability<T, C> {
	private final BlockApiLookup<T, C> lookup;
	private @Nullable Bridge<T, C, ?> bridge;

	private BlockCapability(Identifier name, Class<T> typeClass, Class<C> contextClass) {
		super(name, typeClass, contextClass);
		this.lookup = BlockApiLookup.get(name, typeClass, contextClass);
	}

	/**
	 * Creates a block capability. Capabilities created with the same name share one Fabric lookup (Fabric throws if
	 * the classes differ); declare each one once, as a constant.
	 */
	public static <T, C extends @Nullable Object> BlockCapability<T, C> create(Identifier name, Class<T> typeClass, Class<C> contextClass) {
		return new BlockCapability<>(name, typeClass, contextClass);
	}

	/**
	 * Creates a block capability whose context is the queried side (or {@code null} for an unknown side).
	 */
	public static <T> BlockCapability<T, @Nullable Direction> createSided(Identifier name, Class<T> typeClass) {
		return create(name, typeClass, Direction.class);
	}

	/**
	 * Creates a block capability without context.
	 */
	public static <T> BlockCapability<T, @Nullable Void> createVoid(Identifier name, Class<T> typeClass) {
		return create(name, typeClass, Void.class);
	}

	/**
	 * {@return the capability at the given position, or {@code null}} {@code state} and {@code blockEntity} are looked
	 * up if not passed.
	 */
	public @Nullable T getCapability(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity, C context) {
		return lookup.find(level, pos, state, blockEntity, context);
	}

	/**
	 * Port addition: the Fabric lookup backing this capability.
	 */
	public BlockApiLookup<T, C> lookup() {
		return lookup;
	}

	<E extends BlockEntity> void registerBlockEntity(BlockEntityType<E> type, ICapabilityProvider<? super E, C, T> provider) {
		lookup.registerForBlockEntity(provider::getCapability, type);
		if (bridge != null) {
			bridge.expose(type, provider);
		}
	}

	/**
	 * Port addition: answers queries without a registration here from {@code fabricLookup} (converted with
	 * {@code fromFabric}), and exposes block entity registrations made from now on through {@code fabricLookup}
	 * (converted with {@code toFabric}).
	 */
	public <F> void bridge(BlockApiLookup<F, C> fabricLookup, Function<T, F> toFabric, Function<F, T> fromFabric) {
		if (bridge != null) {
			throw new IllegalStateException(this + " is already bridged to " + bridge.fabricLookup.getId());
		}
		bridge = new Bridge<>(fabricLookup, toFabric);
		lookup.registerFallback((level, pos, state, blockEntity, context) -> {
			F fabric = fabricLookup.find(level, pos, state, blockEntity, context);
			return fabric == null ? null : fromFabric.apply(fabric);
		});
	}

	private record Bridge<T, C extends @Nullable Object, F>(BlockApiLookup<F, C> fabricLookup, Function<T, F> toFabric) {
		<E extends BlockEntity> void expose(BlockEntityType<E> type, ICapabilityProvider<? super E, C, T> provider) {
			fabricLookup.registerForBlockEntity((blockEntity, context) -> {
				T capability = provider.getCapability(blockEntity, context);
				return capability == null ? null : toFabric.apply(capability);
			}, type);
		}
	}
}
