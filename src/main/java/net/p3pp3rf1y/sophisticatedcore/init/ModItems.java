package net.p3pp3rf1y.sophisticatedcore.init;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Item;
import net.p3pp3rf1y.sophisticatedcore.Config;
import net.p3pp3rf1y.sophisticatedcore.fabric.registries.DeferredHolder;
import net.p3pp3rf1y.sophisticatedcore.linkedstorage.EnderLinkerItem;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.fabric.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModItems {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, SophisticatedCore.MOD_ID);
	public static final DeferredHolder<Item, EnderLinkerItem> ENDER_LINKER = ITEMS.register("ender_linker", EnderLinkerItem::new);
	//  is in the IStorageWrapper cycle (see PROGRESS.md).
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
			SophisticatedCore.MOD_ID);
	// NeoForge: CreativeModeTab.builder() (NeoForge-patched no-arg builder) → FabricCreativeModeTab.builder()
	public static final Supplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("main",
			() -> FabricCreativeModeTab.builder().icon(() -> new ItemStack(ModFluids.XP_BUCKET.get())).title(Component.translatable("itemGroup.sophisticatedcore"))
					.displayItems((featureFlags, output) -> {
						output.accept(new ItemStack(ModFluids.XP_BUCKET.get()));
						if (Config.COMMON.enabledItems.isItemEnabled(ENDER_LINKER.get())
				&& (FabricLoader.getInstance().isModLoaded("sophisticatedbackpacks")
				|| FabricLoader.getInstance().isModLoaded("sophisticatedstorage"))) {
			output.accept(ENDER_LINKER.get());
		}
						//  && (sophisticatedbackpacks || sophisticatedstorage is loaded), as in ref-neoforge-core
					}).build());

	private ModItems() {
	}

	public static void register() {
		ITEMS.register();
		CREATIVE_MODE_TABS.register();
	}
}
