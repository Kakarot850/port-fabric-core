package net.p3pp3rf1y.sophisticatedcore.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.p3pp3rf1y.sophisticatedcore.extensions.client.gui.screens.SophisticatedScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Implements NeoForge 26.1.2's {@code Screen#getMinecraft()} patch for {@link SophisticatedScreen}.
 */
@Mixin(Screen.class)
public class ScreenMixin implements SophisticatedScreen {
	@Shadow
	@Final
	protected Minecraft minecraft;

	@Override
	public Minecraft sophisticatedCore_getMinecraft() {
		return minecraft;
	}
}
