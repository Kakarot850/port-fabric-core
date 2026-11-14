package net.p3pp3rf1y.sophisticatedcore.extensions.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedcore.fabric.capabilities.BlockCapability;
import org.jspecify.annotations.Nullable;

/**
 * NeoForge's {@code ILevelExtension#getCapability} overloads, added to {@link Level} through Loom interface injection
 * ({@code mixin.common.LevelMixin} only attaches the interface). Ported call sites {@code level.getCapability(...)}
 * become {@code level.sophisticatedCore_getCapability(...)}.
 */
public interface SophisticatedLevel {
	private Level sophisticatedCore_self() {
		return (Level) this;
	}

	default <T, C extends @Nullable Object> @Nullable T sophisticatedCore_getCapability(BlockCapability<T, C> cap, BlockPos pos, C context) {
		return cap.getCapability(sophisticatedCore_self(), pos, null, null, context);
	}

	default <T, C extends @Nullable Object> @Nullable T sophisticatedCore_getCapability(BlockCapability<T, C> cap, BlockPos pos, @Nullable BlockState state,
			@Nullable BlockEntity blockEntity, C context) {
		return cap.getCapability(sophisticatedCore_self(), pos, state, blockEntity, context);
	}

	default <T> @Nullable T sophisticatedCore_getCapability(BlockCapability<T, @Nullable Void> cap, BlockPos pos) {
		return cap.getCapability(sophisticatedCore_self(), pos, null, null, null);
	}
}
