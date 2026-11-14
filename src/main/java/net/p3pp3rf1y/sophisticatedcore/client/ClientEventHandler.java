package net.p3pp3rf1y.sophisticatedcore.client;

import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.client.init.ModParticles;
import net.p3pp3rf1y.sophisticatedcore.fabric.client.network.ClientPayloadHandlers;
import net.p3pp3rf1y.sophisticatedcore.init.ModFluids;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.StorageSoundHandler;

/**
 * Fabric client entrypoint. Client event wiring from ref-neoforge-core's {@code ClientEventHandler} is ported
 * as the corresponding subsystems land (see PROGRESS.md).
 */
public class ClientEventHandler implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// NeoForge registers ConfigurationScreen as IConfigScreenFactory in the @Mod constructor (Dist.CLIENT, unless
		// "configured" is loaded); on Fabric, Forge Config API Port exposes the same screen through Mod Menu.
		if (!FabricLoader.getInstance().isModLoaded("configured")) {
			ConfigScreenFactoryRegistry.INSTANCE.register(SophisticatedCore.MOD_ID, ConfigurationScreen::new);
		}
		ModParticles.registerFactories();
		registerFluidModels();
		// NeoForge registers clientbound handlers with the payloads (ModPayloads); Fabric needs the client entrypoint.
		ClientPayloadHandlers.register(SophisticatedCore.MOD_ID);
		// NeoForge.EVENT_BUS: StorageSoundHandler::tick (LevelTickEvent.Post), StorageSoundHandler::onWorldUnload (LevelEvent.Unload)
		ClientTickEvents.END_LEVEL_TICK.register(StorageSoundHandler::tick);
		ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, level) -> StorageSoundHandler.onWorldUnload());
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> StorageSoundHandler.onWorldUnload());
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
