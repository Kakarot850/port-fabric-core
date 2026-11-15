package net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.jei;

import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;
import net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.common.CommonPayloads;
import net.p3pp3rf1y.sophisticatedcore.fabric.network.registration.PayloadRegistrar;

public class JeiCompat implements ICompat {
	@Override
	public void init() {
		final PayloadRegistrar registrar = new PayloadRegistrar(SophisticatedCore.MOD_ID).versioned(SophisticatedCore.getNetworkProtocolVersion());
		registrar.optional().playToServer(JeiTransferRecipePayload.TYPE, JeiTransferRecipePayload.STREAM_CODEC, JeiTransferRecipePayload::handlePayload);

		CommonPayloads.registerPackets(registrar);
	}

	@Override
	public void setup() {
		// noop
	}
}
