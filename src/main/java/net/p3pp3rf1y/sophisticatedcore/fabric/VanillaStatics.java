package net.p3pp3rf1y.sophisticatedcore.fabric;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Static members NeoForge 26.1.2 patches into vanilla classes (interface injection can only add instance methods).
 * {@code tools/port_copy.py} {@code CODE_SWAPS} rewrites ported uses: {@code ContextMap.EMPTY} →
 * {@link #EMPTY_CONTEXT_MAP}, {@code DyeColor.getColor(stack)} → {@link #getDyeColor(ItemStack)}.
 */
public final class VanillaStatics {
	/**
	 * NeoForge {@code ContextKeySet.EMPTY}.
	 */
	public static final ContextKeySet EMPTY_CONTEXT_KEY_SET = new ContextKeySet.Builder().build();

	/**
	 * NeoForge {@code ContextMap.EMPTY}.
	 */
	public static final ContextMap EMPTY_CONTEXT_MAP = new ContextMap.Builder().create(EMPTY_CONTEXT_KEY_SET);

	private VanillaStatics() {}

	/**
	 * NeoForge {@code DyeColor#getTag()}: the {@code c:dyes/<color>} item tag.
	 */
	public static TagKey<Item> getDyeTag(DyeColor color) {
		return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "dyes/" + color.getName()));
	}

	/**
	 * NeoForge {@code DyeColor.getColor(ItemStack)}: the stack's {@link DataComponents#DYE} component, else the first
	 * color (in id order, black excluded as in NeoForge) whose {@code c:dyes/<color>} tag contains the stack.
	 */
	public static @Nullable DyeColor getDyeColor(ItemStack stack) {
		DyeColor dyeColor = stack.get(DataComponents.DYE);
		if (dyeColor != null) {
			return dyeColor;
		}

		for (int x = 0; x < DyeColor.BLACK.getId(); x++) {
			DyeColor color = DyeColor.byId(x);
			if (stack.is(getDyeTag(color))) {
				return color;
			}
		}

		return null;
	}
}
