package net.p3pp3rf1y.sophisticatedcore.init;

import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.fabric.network.registration.PayloadRegistrar;
import net.p3pp3rf1y.sophisticatedcore.network.EmitConsumableClientParticlesAndSoundsPayload;
import net.p3pp3rf1y.sophisticatedcore.network.SyncAdditionalSlotInfoPayload;
import net.p3pp3rf1y.sophisticatedcore.network.SyncBlockHighlightsPayload;
import net.p3pp3rf1y.sophisticatedcore.network.SyncContainerClientDataPayload;
import net.p3pp3rf1y.sophisticatedcore.network.SyncEmptySlotIconsPayload;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.PlayDiscPayload;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.StopDiscPlaybackPayload;

public class ModPayloads {
	private ModPayloads() {
	}

	/**
	 * NeoForge: listener for {@code RegisterPayloadHandlersEvent}. Fabric: called from {@code CommonEventHandler} during
	 * {@code onInitialize}; clientbound handlers are handed to {@code ClientPlayNetworking} by
	 * {@code ClientPayloadHandlers.register(MOD_ID)} in the client entrypoint.
	 */
	public static void registerPayloads() {
		final PayloadRegistrar registrar = new PayloadRegistrar(SophisticatedCore.MOD_ID).versioned(SophisticatedCore.getNetworkProtocolVersion());
		registrar.playToServer(SyncContainerClientDataPayload.TYPE, SyncContainerClientDataPayload.STREAM_CODEC, SyncContainerClientDataPayload::handlePayload);
		// TODO(port) pending playToServer: TransferFullSlotPayload (IStorageWrapper cycle)
		// TODO(port) pending playToClient: SyncContainerStacksPayload (IStorageWrapper cycle)
		// TODO(port) pending playToClient: SyncSlotStackPayload (IStorageWrapper cycle)
		// TODO(port) pending playToClient: SyncRecentCraftedResultsPayload (IStorageWrapper cycle)
		// TODO(port) pending playToClient: SyncPlayerSettingsPayload (IStorageWrapper cycle)
		registrar.playToClient(PlayDiscPayload.TYPE, PlayDiscPayload.STREAM_CODEC, PlayDiscPayload::handlePayload);
		registrar.playToClient(StopDiscPlaybackPayload.TYPE, StopDiscPlaybackPayload.STREAM_CODEC, StopDiscPlaybackPayload::handlePayload);
		// TODO(port) pending playToServer: TankClickPayload (IStorageWrapper cycle)
		// TODO(port) pending playToServer: TransferItemsPayload (IStorageWrapper cycle)
		// TODO(port) pending playToClient: SyncTemplateSettingsPayload (IStorageWrapper cycle)
		registrar.playToClient(SyncAdditionalSlotInfoPayload.TYPE, SyncAdditionalSlotInfoPayload.STREAM_CODEC, SyncAdditionalSlotInfoPayload::handlePayload);
		registrar.playToClient(SyncEmptySlotIconsPayload.TYPE, SyncEmptySlotIconsPayload.STREAM_CODEC, SyncEmptySlotIconsPayload::handlePayload);
		// TODO(port) pending playToClient: SyncSlotChangeErrorPayload (IStorageWrapper cycle)
		// TODO(port) pending playToClient: SyncDatapackSettingsTemplatePayload (IStorageWrapper cycle)
		registrar.playToClient(EmitConsumableClientParticlesAndSoundsPayload.TYPE, EmitConsumableClientParticlesAndSoundsPayload.STREAM_CODEC,
				EmitConsumableClientParticlesAndSoundsPayload::handlePayload);
		registrar.playToClient(SyncBlockHighlightsPayload.TYPE, SyncBlockHighlightsPayload.STREAM_CODEC, SyncBlockHighlightsPayload::handlePayload);
		// TODO(port) pending playToServer: RequestLinkerCraftingDiagnosticsPayload (IStorageWrapper cycle)
		// TODO(port) pending playToClient: SyncLinkerCraftingDiagnosticsPayload (IStorageWrapper cycle)
		// TODO(port) pending playToServer: RequestLinkedStorageContentsPayload (IStorageWrapper cycle)
		// TODO(port) pending playToClient: LinkedStorageContentsPayload (IStorageWrapper cycle)
		// TODO(port) pending playToClient: LinkedStorageSettingsPayload (IStorageWrapper cycle)
	}
}
