package net.p3pp3rf1y.sophisticatedcore.client;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.api.IStashStorageItem;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.GuiSoundHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.TranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.client.init.ModParticles;
import net.p3pp3rf1y.sophisticatedcore.client.render.BlockHighlightRenderer;
import net.p3pp3rf1y.sophisticatedcore.client.render.ItemDisplayPreviewRenderer;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import net.p3pp3rf1y.sophisticatedcore.fabric.client.event.RecipesReceivedEvent;
import net.p3pp3rf1y.sophisticatedcore.fabric.client.network.ClientPayloadHandlers;
import net.p3pp3rf1y.sophisticatedcore.init.ModFluids;
import net.p3pp3rf1y.sophisticatedcore.inventory.ItemStackKey;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.StorageSoundHandler;
import net.p3pp3rf1y.sophisticatedcore.util.RecipeHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.Optional;

/**
 * Fabric client entrypoint and client event wiring ported from ref-neoforge-core's {@code ClientEventHandler}.
 */
public class ClientEventHandler implements ClientModInitializer {
	private static final int MIDDLE_BUTTON = 2;
	private static final KeyMapping.Category SOPHISTICATEDCORE_CATEGORY = KeyMapping.Category.register(SophisticatedCore.getIdentifier("main"));
	public static final KeyMapping SORT_KEYBIND = new KeyMapping(TranslationHelper.INSTANCE.translKeybind("sort"),
			InputConstants.Type.MOUSE, MIDDLE_BUTTON, SOPHISTICATEDCORE_CATEGORY);
	public static final KeyMapping TRANSFER_TO_STORAGE_KEYBIND = new KeyMapping(TranslationHelper.INSTANCE.translKeybind("transfer_to_storage"),
			InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, SOPHISTICATEDCORE_CATEGORY);
	public static final KeyMapping TRANSFER_TO_INVENTORY_KEYBIND = new KeyMapping(TranslationHelper.INSTANCE.translKeybind("transfer_to_inventory"),
			InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, SOPHISTICATEDCORE_CATEGORY);

	@Override
	public void onInitializeClient() {
		// NeoForge registers ConfigurationScreen as IConfigScreenFactory in the @Mod constructor (Dist.CLIENT, unless
		// "configured" is loaded); on Fabric, Forge Config API Port exposes the same screen through Mod Menu.
		if (!FabricLoader.getInstance().isModLoaded("configured")) {
			ConfigScreenFactoryRegistry.INSTANCE.register(SophisticatedCore.MOD_ID, ConfigurationScreen::new);
		}
		ModParticles.registerFactories();
		registerFluidModels();
		ConditionalItemModelProperties.ID_MAPPER.put(SophisticatedCore.getIdentifier("ender_linker_bound"), EnderLinkerBound.MAP_CODEC);
		PictureInPictureRendererRegistry.register(ctx -> new ItemDisplayPreviewRenderer(ctx.bufferSource()));
		KeyMappingHelper.registerKeyMapping(SORT_KEYBIND);
		KeyMappingHelper.registerKeyMapping(TRANSFER_TO_STORAGE_KEYBIND);
		KeyMappingHelper.registerKeyMapping(TRANSFER_TO_INVENTORY_KEYBIND);
		// NeoForge registers clientbound handlers with the payloads (ModPayloads); Fabric needs the client entrypoint.
		ClientPayloadHandlers.register(SophisticatedCore.MOD_ID);
		// NeoForge.EVENT_BUS: StorageSoundHandler::tick (LevelTickEvent.Post), StorageSoundHandler::onWorldUnload (LevelEvent.Unload)
		ClientTickEvents.END_LEVEL_TICK.register(StorageSoundHandler::tick);
		ClientTickEvents.END_CLIENT_TICK.register(client -> ItemStackKey.clearCache());
		ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, level) -> StorageSoundHandler.onWorldUnload());
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> StorageSoundHandler.onWorldUnload());
		// NeoForge: ClientEventHandler::recipesReceived and (common bus) RecipeHelper::onRecipesUpdated on RecipesReceivedEvent.
		// The map is set first so recipe change listeners see the new recipes.
		RecipesReceivedEvent.registerClient();
		RecipesReceivedEvent.EVENT.register(event -> RecipeHelper.setRecipes(event.getRecipeMap()));
		RecipesReceivedEvent.EVENT.register(RecipeHelper::onRecipesUpdated);
		LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
			float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
			BlockHighlightRenderer.submit(context.submitNodeCollector(), context.poseStack(), partialTick, context.levelState().cameraRenderState.pos);
		});
		ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof AbstractContainerScreen<?>) {
				ScreenKeyboardEvents.allowKeyPress(screen).register((scr, event) -> handleGuiInput(scr, event,
						SORT_KEYBIND.matches(event),
						TRANSFER_TO_STORAGE_KEYBIND.matches(event),
						TRANSFER_TO_INVENTORY_KEYBIND.matches(event)));
				ScreenMouseEvents.allowMouseClick(screen).register((scr, event) -> handleGuiInput(scr, event,
						SORT_KEYBIND.matchesMouse(event),
						TRANSFER_TO_STORAGE_KEYBIND.matchesMouse(event),
						TRANSFER_TO_INVENTORY_KEYBIND.matchesMouse(event)));
				ScreenEvents.afterExtract(screen).register(ClientEventHandler::onDrawScreen);
			}
		});
		ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipFlag, lines) -> {
			if (!(Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen)) {
				return;
			}
			Slot slot = screen.sophisticatedCore_getSlotUnderMouse();
			if (slot == null || slot.getItem() != stack) {
				return;
			}
			String statusMessage = LinkerCraftingDiagnostics.getStatusMessage(slot.index);
			if (statusMessage != null) {
				lines.add(TranslationHelper.INSTANCE.translStatusMessage(statusMessage).withStyle(ChatFormatting.RED));
			}
		});
	}

	private static boolean handleGuiInput(Screen screen, InputWithModifiers input, boolean matchesSort, boolean matchesToStorage, boolean matchesToInventory) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.hasControlDown() || mc.hasAltDown() || input.hasControlDown() || input.hasAltDown()) {
			return true;
		}
		boolean shiftDown = mc.hasShiftDown() || input.hasShiftDown();
		if (matchesSort && !shiftDown && screen instanceof StorageScreenBase<?> && tryCallSort(screen)) {
			GuiSoundHelper.playButtonClickSound();
			return false;
		} else if (matchesToStorage && screen instanceof AbstractContainerScreen<?> && tryCallTransferToStorage(screen, !shiftDown)) {
			GuiSoundHelper.playButtonClickSound();
			return false;
		} else if (matchesToInventory && screen instanceof AbstractContainerScreen<?> && tryCallTransferToInventory(screen, !shiftDown)) {
			GuiSoundHelper.playButtonClickSound();
			return false;
		}
		return true;
	}

	public static boolean isActiveAndMatchesIgnoringShift(KeyMapping keyMapping, InputConstants.Key key) {
		Minecraft mc = Minecraft.getInstance();
		return keyMapping.sophisticatedCore_isActiveAndMatches(key)
				|| (key != InputConstants.UNKNOWN && key.equals(KeyMappingHelper.getBoundKeyOf(keyMapping))
						&& mc.hasShiftDown() && !mc.hasControlDown() && !mc.hasAltDown());
	}

	private static boolean tryCallTransferToStorage(Screen gui, boolean transferAll) {
		if (gui instanceof StorageScreenBase<?> screen) {
			screen.getMenu().transferItemsToStorage(transferAll);
			return true;
		}
		return false;
	}

	private static boolean tryCallTransferToInventory(Screen gui, boolean transferAll) {
		if (gui instanceof StorageScreenBase<?> screen) {
			screen.getMenu().transferItemsToPlayerInventory(transferAll);
			return true;
		}
		return false;
	}

	private static boolean tryCallSort(Screen gui) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && mc.player.containerMenu instanceof StorageContainerMenuBase<?> container && gui instanceof StorageScreenBase<?> screen) {
			MouseHandler mh = mc.mouseHandler;
			double mouseX = mh.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
			double mouseY = mh.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();
			Slot selectedSlot = screen.getHoveredSlot(mouseX, mouseY);
			if (selectedSlot == null || container.isNotPlayersInventorySlot(selectedSlot.index)) {
				container.sort();
				return true;
			}
		}
		return false;
	}

	private static void onDrawScreen(Screen screen, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickProgress) {
		Minecraft mc = Minecraft.getInstance();
		if (!(screen instanceof AbstractContainerScreen<?> containerGui) || screen instanceof CreativeModeInventoryScreen || mc.player == null) {
			return;
		}
		ItemStack held = containerGui.getMenu().getCarried();
		if (!held.isEmpty()) {
			renderStashTooltip(containerGui, guiGraphics, held, mouseX, mouseY);
		}
	}

	public static void onContainerScreenForeground(AbstractContainerScreen<?> containerGui, GuiGraphicsExtractor guiGraphics) {
		Minecraft mc = Minecraft.getInstance();
		if (containerGui instanceof CreativeModeInventoryScreen || mc.player == null) {
			return;
		}
		AbstractContainerMenu menu = containerGui.getMenu();
		LinkerCraftingDiagnostics.requestIfChanged(menu);
		renderLinkerCraftingDiagnostics(guiGraphics, menu);
		ItemStack held = menu.getCarried();
		if (held.isEmpty()) {
			return;
		}

		Slot under = containerGui.sophisticatedCore_getSlotUnderMouse();
		for (Slot s : menu.slots) {
			ItemStack stack = s.getItem();
			if (s == under || stack.isEmpty()) {
				continue;
			}
			getStashResult(stack, held).filter(stashResult -> s.mayPickup(mc.player))
					.ifPresent(stashResult -> renderStashSign(mc, guiGraphics, s, stack, stashResult));
		}
	}

	private static void renderLinkerCraftingDiagnostics(GuiGraphicsExtractor guiGraphics, AbstractContainerMenu menu) {
		for (Slot slot : menu.slots) {
			renderLinkerCraftingDiagnostic(guiGraphics, slot);
		}
		if (menu instanceof StorageContainerMenuBase<?> storageContainer) {
			storageContainer.upgradeSlots.forEach(slot -> renderLinkerCraftingDiagnostic(guiGraphics, slot));
		}
	}

	private static void renderLinkerCraftingDiagnostic(GuiGraphicsExtractor guiGraphics, Slot slot) {
		if (LinkerCraftingDiagnostics.getStatusMessage(slot.index) != null) {
			guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x80FF0000);
		}
	}

	private static void renderStashTooltip(AbstractContainerScreen<?> containerGui, GuiGraphicsExtractor guiGraphics, ItemStack held, int mouseX, int mouseY) {
		Slot under = containerGui.sophisticatedCore_getSlotUnderMouse();
		if (under != null) {
			ItemStack inInventory = under.getItem();
			if (inInventory.getCount() == 1 && inInventory.getItem() instanceof IStashStorageItem stashStorageItem) {
				renderSpecialTooltip(containerGui, guiGraphics, held, getStashTooltip(inInventory, held, stashStorageItem), mouseX, mouseY);
			} else if (held.getItem() instanceof IStashStorageItem stashStorageItem) {
				renderSpecialTooltip(containerGui, guiGraphics, inInventory, getStashTooltip(held, inInventory, stashStorageItem), mouseX, mouseY);
			}
		}
	}

	private static void renderStashSign(Minecraft mc, GuiGraphicsExtractor guiGraphics, Slot s, ItemStack stack, IStashStorageItem.StashResult stashResult) {
		int x = s.x;
		int y = s.y;

		int color = ARGB.opaque(stashResult == IStashStorageItem.StashResult.MATCH_AND_SPACE ? ChatFormatting.GREEN.getColor() : 0xFFFF00);
		if (stack.getItem() instanceof IStashStorageItem) {
			guiGraphics.text(mc.font, "+", x + 10, y + 8, color);
		} else {
			guiGraphics.text(mc.font, "-", x + 1, y, color);
		}
	}

	private static void renderSpecialTooltip(Screen screen, GuiGraphicsExtractor guiGraphics, ItemStack stack,
			Optional<TooltipComponent> tooltipComponent, int x, int y) {
		GuiHelper.extractTooltip(screen, guiGraphics, stack,
				Collections.singletonList(Component.translatable(TranslationHelper.INSTANCE.translItemTooltip("storage") + ".right_click_to_add_to_storage")),
				tooltipComponent, x, y);
	}

	private static Optional<IStashStorageItem.StashResult> getStashResult(ItemStack inInventory, ItemStack held) {
		if (inInventory.getCount() == 1 && inInventory.getItem() instanceof IStashStorageItem stashStorageItem) {
			return getStashResult(inInventory, held, stashStorageItem);
		}

		if (held.getItem() instanceof IStashStorageItem stashStorageItem) {
			return getStashResult(held, inInventory, stashStorageItem);
		}
		return Optional.empty();
	}

	private static Optional<IStashStorageItem.StashResult> getStashResult(ItemStack potentialStashStorage, ItemStack potentiallyStashable,
			IStashStorageItem stashStorageItem) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return Optional.empty();
		}
		IStashStorageItem.StashResult stashResult = stashStorageItem.getItemStashable(mc.level.registryAccess(), potentialStashStorage,
				potentiallyStashable);
		if (stashResult == IStashStorageItem.StashResult.NO_SPACE) {
			return Optional.empty();
		}
		return Optional.of(stashResult);
	}

	private static Optional<TooltipComponent> getStashTooltip(ItemStack potentialStashStorage, ItemStack potentiallyStashable,
			IStashStorageItem stashStorageItem) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || stashStorageItem.getItemStashable(mc.level.registryAccess(), potentialStashStorage,
				potentiallyStashable) == IStashStorageItem.StashResult.NO_SPACE) {
			return Optional.empty();
		}
		return stashStorageItem.getInventoryTooltip(potentialStashStorage);
	}

	/**
	 * NeoForge: {@code RegisterFluidModelsEvent} plus an {@code IClientFluidTypeExtensions} with the same still/flowing
	 * textures. On Fabric the baked model goes into vanilla's {@code FluidStateModelSet} (fabric-rendering-fluids-v1),
	 * which world rendering uses and from which GUI code reads sprites; Transfer API's
	 * {@code FluidVariantRendering#getColor} reads its tint. NeoForge's {@code FluidTintSource} lambda returned a constant
	 * {@code 0xD9FFFFFF}, i.e. {@link BlockTintSources#constant(int)}.
	 */
	private static void registerFluidModels() {
		FluidRenderingRegistry.register(ModFluids.XP_STILL.get(), ModFluids.XP_FLOWING.get(),
				new FluidModel.Unbaked(
						new Material(Identifier.fromNamespaceAndPath(SophisticatedCore.MOD_ID, "block/xp_still")),
						new Material(Identifier.fromNamespaceAndPath(SophisticatedCore.MOD_ID, "block/xp_flowing")),
						null, BlockTintSources.constant(0xD9FFFFFF)));
	}
}
