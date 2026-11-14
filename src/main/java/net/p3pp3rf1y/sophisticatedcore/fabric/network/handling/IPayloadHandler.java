package net.p3pp3rf1y.sophisticatedcore.fabric.network.handling;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Fabric stand-in for NeoForge's {@code net.neoforged.neoforge.network.handling.IPayloadHandler}.
 */
@FunctionalInterface
public interface IPayloadHandler<T extends CustomPacketPayload> {
	void handle(T payload, IPayloadContext context);
}
