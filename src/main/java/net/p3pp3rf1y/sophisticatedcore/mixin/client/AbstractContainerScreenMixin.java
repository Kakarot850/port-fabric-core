package net.p3pp3rf1y.sophisticatedcore.mixin.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.p3pp3rf1y.sophisticatedcore.client.ClientEventHandler;
import net.p3pp3rf1y.sophisticatedcore.client.gui.SettingsScreen;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.extensions.client.gui.screens.inventory.SophisticatedAbstractContainerScreen;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Implements NeoForge 26.1.2's {@code AbstractContainerScreen} getters and foreground hook for {@link SophisticatedAbstractContainerScreen}.
 */
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin implements SophisticatedAbstractContainerScreen {
	@Shadow
	protected int leftPos;
	@Shadow
	protected int topPos;
	@Shadow
	protected int imageWidth;
	@Shadow
	protected int imageHeight;
	@Shadow
	protected @Nullable Slot hoveredSlot;

	@Override
	public int sophisticatedCore_getGuiLeft() {
		return leftPos;
	}

	@Override
	public int sophisticatedCore_getGuiTop() {
		return topPos;
	}

	@Override
	public int sophisticatedCore_getXSize() {
		return imageWidth;
	}

	@Override
	public int sophisticatedCore_getYSize() {
		return imageHeight;
	}

	@Override
	public @Nullable Slot sophisticatedCore_getSlotUnderMouse() {
		return hoveredSlot;
	}

	@Inject(method = "extractContents", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;popMatrix()Lorg/joml/Matrix3x2fStack;"))
	private void sophisticatedCore$onForeground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		if (!((Object) this instanceof StorageScreenBase<?> || (Object) this instanceof SettingsScreen)) {
			ClientEventHandler.onContainerScreenForeground((AbstractContainerScreen<?>) (Object) this, graphics);
		}
	}
}
