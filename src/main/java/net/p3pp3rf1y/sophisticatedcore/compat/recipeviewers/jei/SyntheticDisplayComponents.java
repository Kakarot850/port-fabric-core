package net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.jei;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.init.ModCoreDataComponents;

import java.util.List;

final class SyntheticDisplayComponents {
	private static final List<DataComponentType<?>> COMPONENTS = List.of(ModCoreDataComponents.MAIN_COLOR,
			ModCoreDataComponents.ACCENT_COLOR, ModCoreDataComponents.RENDER_DATA);

	private SyntheticDisplayComponents() {
	}

	static boolean hasAny(ItemStack stack) {
		return COMPONENTS.stream().anyMatch(stack::has);
	}
}
