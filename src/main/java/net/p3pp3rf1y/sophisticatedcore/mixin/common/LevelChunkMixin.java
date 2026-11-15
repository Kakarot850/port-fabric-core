package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
	@Shadow
	@Final
	private Level level;

	@Shadow
	private boolean isInLevel() {
		throw new AssertionError();
	}

	@Shadow
	public abstract Map<BlockPos, BlockEntity> getBlockEntities();

	@Inject(method = "addAndRegisterBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("TAIL"))
	private void sophisticatedCore_onAddAndRegisterBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
		if (isInLevel()) {
			ChunkPos pos = ((LevelChunk) (Object) this).getPos();
			if (level instanceof ServerLevel serverLevel && serverLevel.getChunkSource().getChunkNow(pos.x(), pos.z()) == null) {
				SophisticatedCore.enqueuePendingChunkLoad(serverLevel, pos);
			} else {
				blockEntity.sophisticatedCore_safeLoad();
			}
		}
	}

	@Inject(method = "clearAllBlockEntities()V", at = @At("HEAD"))
	private void sophisticatedCore_onClearAllBlockEntities(CallbackInfo ci) {
		for (BlockEntity blockEntity : getBlockEntities().values()) {
			blockEntity.sophisticatedCore_safeUnload();
		}
	}
}
