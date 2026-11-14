package net.p3pp3rf1y.sophisticatedcore.fabric.capabilities;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.capabilities.ItemCapability}: a capability queried for an item
 * stack, usually with an {@code ItemAccess} context, e.g. {@link Capabilities.Item#ITEM}.
 *
 * <p>Port: backed by a Fabric {@link ItemApiLookup} with the same id and classes. {@link #fallback} adds a fallback
 * provider (used to answer from Fabric's {@code ItemStorage.ITEM} / {@code FluidStorage.ITEM}).
 */
public final class ItemCapability<T, C extends @Nullable Object> extends BaseCapability<T, C> {
	private final ItemApiLookup<T, C> lookup;

	private ItemCapability(Identifier name, Class<T> typeClass, Class<C> contextClass) {
		super(name, typeClass, contextClass);
		this.lookup = ItemApiLookup.get(name, typeClass, contextClass);
	}

	public static <T, C extends @Nullable Object> ItemCapability<T, C> create(Identifier name, Class<T> typeClass, Class<C> contextClass) {
		return new ItemCapability<>(name, typeClass, contextClass);
	}

	public static <T> ItemCapability<T, @Nullable Void> createVoid(Identifier name, Class<T> typeClass) {
		return create(name, typeClass, Void.class);
	}

	/**
	 * {@return the capability of the stack, or {@code null}} Empty stacks have no capabilities.
	 */
	public @Nullable T getCapability(ItemStack stack, C context) {
		return stack.isEmpty() ? null : lookup.find(stack, context);
	}

	/**
	 * Port addition: the Fabric lookup backing this capability.
	 */
	public ItemApiLookup<T, C> lookup() {
		return lookup;
	}

	/**
	 * Port addition: registers a provider queried for items without a registration of their own.
	 */
	public void fallback(BiFunction<ItemStack, C, @Nullable T> provider) {
		lookup.registerFallback(provider::apply);
	}

	void registerItems(ICapabilityProvider<ItemStack, C, T> provider, ItemLike... items) {
		lookup.registerForItems(provider::getCapability, items);
	}
}
