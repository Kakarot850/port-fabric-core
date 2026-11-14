package net.p3pp3rf1y.sophisticatedcore.crafting;

import net.minecraft.world.item.ItemStack;

import java.util.List;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;

public interface IExactDisplayStacksIngredient extends CustomIngredient {
	List<ItemStack> getExactDisplayStacks();
}
