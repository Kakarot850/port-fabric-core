package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
	@Shadow
	private boolean isInLevel() {
		throw new AssertionError();
	}

	@Shadow
	public abstract Map<BlockPos, BlockEntity> getBlockEntities();

	@Inject(method = "addAndRegisterBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("TAIL"))
	private void sophisticatedCore_onAddAndRegisterBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
		if (isInLevel()) {
			blockEntity.sophisticatedCore_onLoad();
		}
	}

	@Inject(method = "registerAllBlockEntitiesAfterLevelLoad()V", at = @At("TAIL"))
	private void sophisticatedCore_onRegisterAllBlockEntitiesAfterLevelLoad(CallbackInfo ci) {
		for (BlockEntity blockEntity : getBlockEntities().values()) {
			blockEntity.sophisticatedCore_onLoad();
		}
	}

	@Inject(method = "clearAllBlockEntities()V", at = @At("HEAD"))
	private void sophisticatedCore_onClearAllBlockEntities(CallbackInfo ci) {
		for (BlockEntity blockEntity : getBlockEntities().values()) {
			blockEntity.sophisticatedCore_onChunkUnloaded();
		}
	}
}
