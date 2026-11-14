package net.p3pp3rf1y.sophisticatedcore.fabric.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fabric stand-in for NeoForge's {@code net.neoforged.neoforge.registries.DeferredRegister}.
 * <p>
 * Entries are declared with lazy factories and registered into the vanilla registry, in declaration order, when
 * {@link #register()} is called from a mod initializer (NeoForge does this from {@code RegisterEvent}). Keeping the
 * factories lazy preserves NeoForge's semantics: a factory may read other holders (e.g. a fluid's bucket item), and
 * ported call sites keep using {@code holder.get()}.
 * <p>
 * Registries whose values are read straight from vanilla APIs (data component types) are registered eagerly instead;
 * see {@code ModCoreDataComponents}.
 */
public class DeferredRegister<T> {
	private final ResourceKey<? extends Registry<T>> registryKey;
	private final String namespace;
	private final Map<DeferredHolder<T, ? extends T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
	private boolean registered = false;

	protected DeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		this.registryKey = Objects.requireNonNull(registryKey);
		this.namespace = Objects.requireNonNull(namespace);
	}

	public static <T> DeferredRegister<T> create(Registry<T> registry, String namespace) {
		return new DeferredRegister<>(registry.key(), namespace);
	}

	public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> key, String namespace) {
		return new DeferredRegister<>(key, namespace);
	}

	public static Items createItems(String namespace) {
		return new Items(namespace);
	}

	public <I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> sup) {
		Objects.requireNonNull(sup);
		return register(name, key -> sup.get());
	}

	public <I extends T> DeferredHolder<T, I> register(String name, Function<Identifier, ? extends I> func) {
		if (registered) {
			throw new IllegalStateException("Cannot add entries to DeferredRegister " + registryKey.identifier() + " after register() was called.");
		}
		Objects.requireNonNull(name);
		Objects.requireNonNull(func);
		Identifier id = Identifier.fromNamespaceAndPath(namespace, name);
		DeferredHolder<T, I> holder = new DeferredHolder<>(ResourceKey.create(registryKey, id));
		if (entries.putIfAbsent(holder, () -> func.apply(id)) != null) {
			throw new IllegalArgumentException("Duplicate registration " + name);
		}
		return holder;
	}

	/**
	 * Creates every declared entry and registers it. Call once, from {@code ModInitializer#onInitialize}.
	 */
	public void register() {
		if (registered) {
			throw new IllegalStateException("DeferredRegister " + registryKey.identifier() + " for " + namespace + " was already registered.");
		}
		registered = true;
		Registry<T> registry = getRegistry();
		entries.forEach((holder, factory) -> bind(registry, holder, factory));
	}

	@SuppressWarnings("unchecked")
	private static <T, I extends T> void bind(Registry<T> registry, DeferredHolder<T, I> holder, Supplier<? extends T> factory) {
		holder.bind(registry, (I) factory.get());
	}

	@SuppressWarnings("unchecked")
	public Registry<T> getRegistry() {
		Registry<?> registry = BuiltInRegistries.REGISTRY.getValue(registryKey.identifier());
		if (registry == null) {
			throw new IllegalStateException("Unknown registry " + registryKey.identifier());
		}
		return (Registry<T>) registry;
	}

	public ResourceKey<? extends Registry<T>> getRegistryKey() {
		return registryKey;
	}

	public String getNamespace() {
		return namespace;
	}

	public Collection<DeferredHolder<T, ? extends T>> getEntries() {
		return Collections.unmodifiableSet(entries.keySet());
	}

	/**
	 * Mirrors NeoForge's {@code DeferredRegister.Items}: {@link #registerItem} sets the item id on the properties,
	 * which 26.1 items require before construction.
	 */
	public static class Items extends DeferredRegister<Item> {
		protected Items(String namespace) {
			super(Registries.ITEM, namespace);
		}

		public <I extends Item> DeferredHolder<Item, I> registerItem(String name, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> properties) {
			return register(name, key -> func.apply(properties.get().setId(ResourceKey.create(Registries.ITEM, key))));
		}

		public <I extends Item> DeferredHolder<Item, I> registerItem(String name, Function<Item.Properties, ? extends I> func) {
			return registerItem(name, func, Item.Properties::new);
		}
	}
}
