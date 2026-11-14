package net.p3pp3rf1y.sophisticatedcore.extensions.client.gui.screens;

import net.minecraft.client.Minecraft;

/**
 * NeoForge's {@code Screen#getMinecraft()} patch (public getter for the protected {@code minecraft} field), added through
 * Loom interface injection and implemented by {@code mixin.client.ScreenMixin}. {@code tools/port_copy.py} renames
 * ported {@code screen.getMinecraft()} calls.
 */
public interface SophisticatedScreen {
	default Minecraft sophisticatedCore_getMinecraft() {
		throw new UnsupportedOperationException("Implemented by ScreenMixin");
	}
}
