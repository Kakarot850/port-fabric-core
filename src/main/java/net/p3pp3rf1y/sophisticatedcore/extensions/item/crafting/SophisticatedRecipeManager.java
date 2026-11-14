package net.p3pp3rf1y.sophisticatedcore.extensions.item.crafting;

import net.minecraft.world.item.crafting.RecipeMap;

/**
 * NeoForge's {@code RecipeManager#recipeMap()} patch (getter for the private {@code recipes} field), added through Loom
 * interface injection and implemented by {@code mixin.common.RecipeManagerMixin}. Ported call sites
 * {@code recipeManager.recipeMap()} become {@code recipeManager.sophisticatedCore_recipeMap()}.
 */
public interface SophisticatedRecipeManager {
	default RecipeMap sophisticatedCore_recipeMap() {
		throw new UnsupportedOperationException("Implemented by RecipeManagerMixin");
	}
}
