package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.p3pp3rf1y.sophisticatedcore.extensions.item.crafting.SophisticatedRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Implements NeoForge 26.1.2's {@code RecipeManager#recipeMap()} patch for {@link SophisticatedRecipeManager}.
 */
@Mixin(RecipeManager.class)
public class RecipeManagerMixin implements SophisticatedRecipeManager {
	@Shadow
	private RecipeMap recipes;

	@Override
	public RecipeMap sophisticatedCore_recipeMap() {
		return recipes;
	}
}
