package net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.common;

import net.p3pp3rf1y.sophisticatedcore.fabric.network.registration.PayloadRegistrar;

public class CommonPayloads {
	private static boolean registered = false;

	public static void registerPackets(final PayloadRegistrar registrar) {
		if (registered) {
			return;
		}

		registered = true;
		registrar.optional().playToServer(SetGhostSlotPayload.TYPE, SetGhostSlotPayload.STREAM_CODEC, SetGhostSlotPayload::handlePayload);
		registrar.optional().playToServer(SetMemorySlotPayload.TYPE, SetMemorySlotPayload.STREAM_CODEC, SetMemorySlotPayload::handlePayload);
	}
}
