package net.p3pp3rf1y.sophisticatedcore.fabric.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Objects;

/**
 * Fabric stand-in for NeoForge's {@code net.neoforged.neoforge.client.network.ClientPacketDistributor}. NeoForge sends
 * through {@code ClientPacketListener#send(CustomPacketPayload)}; Fabric's {@link ClientPlayNetworking#send} is the
 * equivalent (the payload type must be registered for {@code serverboundPlay()}).
 */
public final class ClientPacketDistributor {
	private ClientPacketDistributor() {
	}

	public static void sendToServer(CustomPacketPayload payload, CustomPacketPayload... payloads) {
		Objects.requireNonNull(payload, "Cannot send null payload");
		ClientPlayNetworking.send(payload);
		for (CustomPacketPayload otherPayload : payloads) {
			Objects.requireNonNull(otherPayload, "Cannot send null payload");
			ClientPlayNetworking.send(otherPayload);
		}
	}
}
