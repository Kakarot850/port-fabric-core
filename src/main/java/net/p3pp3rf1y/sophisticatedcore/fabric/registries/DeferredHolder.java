package net.p3pp3rf1y.sophisticatedcore.fabric.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Fabric stand-in for NeoForge's {@code DeferredHolder}: a {@link Supplier} for an entry declared through
 * {@link DeferredRegister}, bound when that register's {@link DeferredRegister#register()} runs.
 * <p>
 * Only the parts of NeoForge's API that ported code uses are implemented. {@link #getDelegate()} exposes the vanilla
 * {@link Holder.Reference} for code that needs holder semantics.
 */
public class DeferredHolder<R, T extends R> implements Supplier<T> {
	private final ResourceKey<R> key;
	private @Nullable T value;
	private Holder.@Nullable Reference<T> delegate;

	DeferredHolder(ResourceKey<R> key) {
		this.key = Objects.requireNonNull(key);
	}

	void bind(Registry<R> registry, T value) {
		if (this.value != null) {
			throw new IllegalStateException("Registry entry " + key.identifier() + " is already bound");
		}
		this.delegate = Registry.registerForHolder(registry, key, value);
		this.value = value;
	}

	@Override
	public T get() {
		T current = value;
		if (current == null) {
			throw new IllegalStateException("Registry entry " + key.identifier() + " is not registered yet");
		}
		return current;
	}

	public T value() {
		return get();
	}

	public Optional<T> asOptional() {
		return Optional.ofNullable(value);
	}

	public boolean isBound() {
		return value != null;
	}

	public Identifier getId() {
		return key.identifier();
	}

	public ResourceKey<R> getKey() {
		return key;
	}

	public Holder<T> getDelegate() {
		Holder.Reference<T> current = delegate;
		if (current == null) {
			throw new IllegalStateException("Registry entry " + key.identifier() + " is not registered yet");
		}
		return current;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj instanceof DeferredHolder<?, ?> other && key.equals(other.key);
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public String toString() {
		return "DeferredHolder{" + key + "}";
	}
}
