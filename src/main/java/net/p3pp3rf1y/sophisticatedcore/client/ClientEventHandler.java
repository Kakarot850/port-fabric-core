package net.p3pp3rf1y.sophisticatedcore.client;

import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;

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
	}
}
