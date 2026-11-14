package net.p3pp3rf1y.sophisticatedcore.fabric.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * Stand-in for NeoForge's {@code net.neoforged.neoforge.capabilities.IBlockCapabilityProvider}: computes a block
 * capability from the block's position and state (for blocks with or without a block entity).
 */
@FunctionalInterface
public interface IBlockCapabilityProvider<T, C extends @Nullable Object> {
	@Nullable
	T getCapability(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, C context);
}
