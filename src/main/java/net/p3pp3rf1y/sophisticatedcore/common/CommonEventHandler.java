package net.p3pp3rf1y.sophisticatedcore.common;

import net.p3pp3rf1y.sophisticatedcore.init.ModFluids;
import net.p3pp3rf1y.sophisticatedcore.init.ModParticles;

public class CommonEventHandler {
	public void registerHandlers() {
		ModFluids.registerHandlers();
		ModParticles.registerParticles();
		// TODO(port): ModRecipes.registerHandlers(), ModPayloads.registerPayloads and the NeoForge.EVENT_BUS listeners
		//  (server tick end, datapack sync/recipes updated, storage sounds, magnet, fake player, login/logout, ender link crafted)
		//  are wired in as their subsystems are ported; see PROGRESS.md.
	}
}
