package net.p3pp3rf1y.sophisticatedcore.fabric.network.codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Fabric stand-in for NeoForge's {@code net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs}, adapted from NeoForge
 * 26.1.2.53-beta (LGPL-2.1). Only {@link #enumCodec} is used by Sophisticated Core and Storage, so only it is ported.
 */
public final class NeoForgeStreamCodecs {
	private NeoForgeStreamCodecs() {
	}

	public static <B extends FriendlyByteBuf, V extends Enum<V>> StreamCodec<B, V> enumCodec(Class<V> enumClass) {
		return new StreamCodec<>() {
			@Override
			public V decode(B buf) {
				return buf.readEnum(enumClass);
			}

			@Override
			public void encode(B buf, V value) {
				buf.writeEnum(value);
			}
		};
	}
}
