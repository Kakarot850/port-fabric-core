package net.p3pp3rf1y.sophisticatedcore.extensions.client.gui.screens.inventory;

import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

/**
 * NeoForge's {@code AbstractContainerScreen} getters ({@code getGuiLeft}, {@code getGuiTop}, {@code getXSize},
 * {@code getYSize} for the protected {@code leftPos}, {@code topPos}, {@code imageWidth}, {@code imageHeight}), added
 * through Loom interface injection and implemented by {@code mixin.client.AbstractContainerScreenMixin}.
 * {@code tools/port_copy.py} renames ported calls.
 */
public interface SophisticatedAbstractContainerScreen {
	default int sophisticatedCore_getGuiLeft() {
		throw new UnsupportedOperationException("Implemented by AbstractContainerScreenMixin");
	}

	default int sophisticatedCore_getGuiTop() {
		throw new UnsupportedOperationException("Implemented by AbstractContainerScreenMixin");
	}

	default int sophisticatedCore_getXSize() {
		throw new UnsupportedOperationException("Implemented by AbstractContainerScreenMixin");
	}

	default int sophisticatedCore_getYSize() {
		throw new UnsupportedOperationException("Implemented by AbstractContainerScreenMixin");
	}

	@Nullable
	default Slot sophisticatedCore_getSlotUnderMouse() {
		throw new UnsupportedOperationException("Implemented by AbstractContainerScreenMixin");
	}
}
