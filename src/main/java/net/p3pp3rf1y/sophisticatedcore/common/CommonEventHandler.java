package net.p3pp3rf1y.sophisticatedcore.common;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.p3pp3rf1y.sophisticatedcore.fabric.event.OnDatapackSyncEvent;
import net.p3pp3rf1y.sophisticatedcore.init.ModFluids;
import net.p3pp3rf1y.sophisticatedcore.init.ModParticles;
import net.p3pp3rf1y.sophisticatedcore.init.ModPayloads;
import net.p3pp3rf1y.sophisticatedcore.init.ModRecipes;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.ServerStorageSoundHandler;
import net.p3pp3rf1y.sophisticatedcore.util.CoreFakePlayer;
import net.p3pp3rf1y.sophisticatedcore.util.RecipeHelper;

public class CommonEventHandler {
	public void registerHandlers() {
		ModFluids.registerHandlers();
		ModParticles.registerParticles();
		ModRecipes.registerHandlers();
		ModPayloads.registerPayloads();
		// NeoForge.EVENT_BUS listeners -> Fabric events
		OnDatapackSyncEvent.register();
		OnDatapackSyncEvent.EVENT.register(RecipeHelper::onDataPackSync);
		// RecipeHelper::onRecipesUpdated (client-only RecipesReceivedEvent) is registered by ClientEventHandler
		ServerLevelEvents.UNLOAD.register(ServerStorageSoundHandler::onWorldUnload);
		ServerTickEvents.END_LEVEL_TICK.register(ServerStorageSoundHandler::tick);
		ServerLevelEvents.UNLOAD.register(CoreFakePlayer::onDimensionUnload);
		// TODO(port): remaining NeoForge.EVENT_BUS listeners (server tick end, magnet, login/logout, ender link crafted)
		//  are wired in as their subsystems are ported; see PROGRESS.md.
	}
}
