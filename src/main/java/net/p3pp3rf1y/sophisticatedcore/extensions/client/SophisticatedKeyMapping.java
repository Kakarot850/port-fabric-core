package net.p3pp3rf1y.sophisticatedcore.extensions.client;

import com.mojang.blaze3d.platform.InputConstants;

/**
 * NeoForge's {@code IKeyMappingExtension#isActiveAndMatches(InputConstants.Key)}, added to {@code KeyMapping} through
 * Loom interface injection and implemented by {@code mixin.client.KeyMappingMixin}. Fabric has no key conflict
 * contexts or modifiers, so it is NeoForge's check without those two terms: the key is bound and equals the mapping's
 * current key. {@code tools/port_copy.py} renames ported calls.
 */
public interface SophisticatedKeyMapping {
	default boolean sophisticatedCore_isActiveAndMatches(InputConstants.Key keyCode) {
		throw new UnsupportedOperationException("Implemented by KeyMappingMixin");
	}
}
