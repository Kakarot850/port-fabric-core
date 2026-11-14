package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.world.item.Item;
import net.p3pp3rf1y.sophisticatedcore.extensions.item.SophisticatedItem;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Function;

/**
 * Implements NeoForge 26.1.2's {@code Item#computeDefaultResource} / {@code resetDefaultResource} patch for
 * {@link SophisticatedItem}.
 */
@Mixin(Item.class)
public class ItemMixin implements SophisticatedItem {
	@Unique
	private @Nullable ItemResource sophisticatedCore_defaultResource;

	@Override
	public ItemResource sophisticatedCore_computeDefaultResource(Function<Item, ItemResource> resourceConstructor) {
		if (sophisticatedCore_defaultResource == null) {
			sophisticatedCore_defaultResource = resourceConstructor.apply((Item) (Object) this);
		}
		return sophisticatedCore_defaultResource;
	}

	@Override
	public void sophisticatedCore_resetDefaultResource() {
		sophisticatedCore_defaultResource = null;
	}
}
