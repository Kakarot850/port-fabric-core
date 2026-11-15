package net.p3pp3rf1y.sophisticatedcore.compat.mousetweaks;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;

public class MouseTweaksCompat implements ICompat {
	@Override
	public void setup() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			MouseTweaksCompatClient.restrictSophisticatedScrollInteraction();
		}
	}
}
