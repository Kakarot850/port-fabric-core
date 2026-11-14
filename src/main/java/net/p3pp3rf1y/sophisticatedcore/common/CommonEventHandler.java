package net.p3pp3rf1y.sophisticatedcore.common;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.p3pp3rf1y.sophisticatedcore.crafting.EnderLinkerClearRecipe;
import net.p3pp3rf1y.sophisticatedcore.crafting.EnderLinkerEndpointRecipe;
import net.p3pp3rf1y.sophisticatedcore.extensions.item.SophisticatedItem;
import net.p3pp3rf1y.sophisticatedcore.inventory.ItemStackKey;
import net.p3pp3rf1y.sophisticatedcore.upgrades.blockconverter.RecentCraftedResultStorage;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeWrapper;

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
		ServerTickEvents.START_LEVEL_TICK.register(MagnetUpgradeWrapper::globalPostTick);
		ServerLevelEvents.UNLOAD.register((server, level) -> MagnetUpgradeWrapper.onWorldUnload());
		ServerTickEvents.END_SERVER_TICK.register(CommonEventHandler::onTickEnd);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> RecentCraftedResultStorage.syncToPlayer(handler.player));
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> EnderLinkerEndpointRecipe.clearInFlightClaim(handler.player.getUUID()));
		UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
			if (player.isSpectator()) {
				return InteractionResult.PASS;
			}
			ItemStack stack = player.getItemInHand(hand);
			if (stack.getItem() instanceof SophisticatedItem sophisticatedItem) {
				return sophisticatedItem.sophisticatedCore_onItemUseFirst(stack, new UseOnContext(player, hand, hitResult));
			}
			return InteractionResult.PASS;
		});
	}

	public static void onEnderLinkCrafted(Player player, Container inventory, ItemStack crafting) {
		if (player.level() instanceof ServerLevel level) {
			EnderLinkerClearRecipe.clearPendingCraftClaim(level, inventory);
			EnderLinkerEndpointRecipe.issueCraftClaim(player, crafting);
			if (EnderLinkerEndpointRecipe.completeCraft(level, player, inventory, crafting)) {
				EnderLinkerEndpointRecipe.finalizeDeliveredCraftResult(level, crafting, player.getInventory(), player.containerMenu.getCarried());
			}
		}
	}

	private static void onTickEnd(MinecraftServer server) {
		ItemStackKey.clearCache();
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			EnderLinkerEndpointRecipe.finalizePendingCraftResult(player.level(), player.containerMenu.getCarried());
		}
		EnderLinkerEndpointRecipe.clearInFlightClaims();
	}
}
