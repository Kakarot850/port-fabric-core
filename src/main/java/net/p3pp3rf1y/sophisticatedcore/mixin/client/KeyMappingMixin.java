package net.p3pp3rf1y.sophisticatedcore.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.p3pp3rf1y.sophisticatedcore.extensions.client.SophisticatedKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Implements NeoForge 26.1.2's {@code IKeyMappingExtension#isActiveAndMatches} for {@link SophisticatedKeyMapping}
 * (without NeoForge's conflict-context and modifier checks, which Fabric does not have).
 */
@Mixin(KeyMapping.class)
public class KeyMappingMixin implements SophisticatedKeyMapping {
	@Shadow
	protected InputConstants.Key key;

	@Override
	public boolean sophisticatedCore_isActiveAndMatches(InputConstants.Key keyCode) {
		return keyCode != InputConstants.UNKNOWN && keyCode.equals(key);
	}
}
