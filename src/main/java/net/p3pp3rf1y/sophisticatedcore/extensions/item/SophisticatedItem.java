package net.p3pp3rf1y.sophisticatedcore.extensions.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;

import java.util.function.Function;

/**
 * NeoForge's {@code Item} patch caching the component-less {@link ItemResource} per item
 * ({@code computeDefaultResource}, {@code resetDefaultResource}), added through Loom interface injection and
 * implemented by {@code mixin.common.ItemMixin}. Internal to the transfer stand-ins: use {@link ItemResource#of}.
 */
public interface SophisticatedItem {
	/**
	 * {@return the cached default resource of this item, created with {@code resourceConstructor} on first use}
	 */
	default ItemResource sophisticatedCore_computeDefaultResource(Function<Item, ItemResource> resourceConstructor) {
		throw new UnsupportedOperationException("Implemented by ItemMixin");
	}

	/**
	 * Drops the cached default resource; called whenever the default data components are rebound.
	 */
	default void sophisticatedCore_resetDefaultResource() {
		throw new UnsupportedOperationException("Implemented by ItemMixin");
	}

	/**
	 * Resets the default resource of every item. NeoForge does this on {@code DefaultDataComponentsBoundEvent} when
	 * {@code shouldUpdateStaticData()}; the port calls it from {@code mixin.common.ReloadableServerResourcesMixin}
	 * (server data load) and {@code mixin.client.RegistryDataCollectorMixin} (registries received from a remote
	 * server), the two places NeoForge fires that event.
	 */
	static void resetAllDefaultResources() {
		BuiltInRegistries.ITEM.forEach(SophisticatedItem::sophisticatedCore_resetDefaultResource);
	}
}
