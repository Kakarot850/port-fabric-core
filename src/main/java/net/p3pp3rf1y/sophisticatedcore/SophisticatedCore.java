package net.p3pp3rf1y.sophisticatedcore;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.fml.config.ModConfig;
import net.p3pp3rf1y.sophisticatedcore.common.CommonEventHandler;
import net.p3pp3rf1y.sophisticatedcore.init.ModCoreDataComponents;
import net.p3pp3rf1y.sophisticatedcore.init.ModItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

/**
 * Fabric common entrypoint (replaces NeoForge's {@code @Mod} constructor).
 * Registries/networking/events are wired in as their subsystems are ported (see PROGRESS.md).
 */
public class SophisticatedCore implements ModInitializer {
	public static final String MOD_ID = "sophisticatedcore";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	private static String networkProtocolVersion;
	@Nullable
	private static MinecraftServer currentServer = null;
	public final CommonEventHandler commonEventHandler = new CommonEventHandler();

	@Override
	public void onInitialize() {
		networkProtocolVersion = FabricLoader.getInstance().getModContainer(MOD_ID)
				.map(c -> c.getMetadata().getVersion().getFriendlyString())
				.orElse("1");
		ConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, Config.COMMON_SPEC);
		ConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
		commonEventHandler.registerHandlers();
		Config.COMMON.initListeners();
		ModCoreDataComponents.register();
		ModItems.register();

		// NeoForge: ServerLifecycleHooks tracks the server; Fabric has no equivalent, so it is tracked here (as in ref-fabric-core).
		ServerLifecycleEvents.SERVER_STARTING.register(server -> currentServer = server);
		ServerLifecycleEvents.SERVER_STARTED.register(SophisticatedCore::serverStarted);
		ServerLifecycleEvents.SERVER_STOPPED.register(SophisticatedCore::serverStopped);
	}

	private static void serverStarted(MinecraftServer server) {
		ServerLevel world = server.getLevel(Level.OVERWORLD);
		if (world != null) {
			// TODO(port): StorageWrapperRepository.clearCache() once inventory/ is ported
			Config.COMMON.saveIfChanged();
		}
	}

	private static void serverStopped(MinecraftServer server) {
		// TODO(port): StorageWrapperRepository.clearCache() and RecipeHelper.clearListeners() once inventory/ and util/RecipeHelper are ported
		currentServer = null;
	}

	@Nullable
	public static MinecraftServer getCurrentServer() {
		return currentServer;
	}

	/**
	 * Replacement for NeoForge's {@code Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER}.
	 * Only the server's own thread is treated as the logical server. ref-fabric-core also matched "Netty Server IO" thread
	 * names, but in 26.1.2 {@code EventLoopGroupHolder} shares IO threads between client and server
	 * ("Netty NIO|Epoll|Kqueue|Local IO #n"), so a thread name no longer identifies the logical side.
	 */
	public static boolean isLogicalServerThread() {
		MinecraftServer server = currentServer;
		return server != null && server.getRunningThread() == Thread.currentThread();
	}

	public static Identifier getIdentifier(String regName) {
		return Identifier.parse(getRegistryName(regName));
	}

	public static String getRegistryName(String regName) {
		return MOD_ID + ":" + regName;
	}

	public static String getNetworkProtocolVersion() {
		return networkProtocolVersion;
	}
}
