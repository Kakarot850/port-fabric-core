package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.p3pp3rf1y.sophisticatedcore.extensions.entity.SophisticatedEntity;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements SophisticatedEntity {
	@Unique
	private static final String SOPHISTICATED_CORE_DATA_KEY = "SophisticatedCoreData";

	@Unique
	private @Nullable CompoundTag sophisticatedCore_customData;

	@Override
	public CompoundTag sophisticatedCore_getCustomData() {
		if (sophisticatedCore_customData == null) {
			sophisticatedCore_customData = new CompoundTag();
		}
		return sophisticatedCore_customData;
	}

	@Inject(
			method = "saveWithoutId(Lnet/minecraft/world/level/storage/ValueOutput;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V"
			)
	)
	private void sophisticatedCore_saveCustomData(ValueOutput output, CallbackInfo ci) {
		if (sophisticatedCore_customData != null && !sophisticatedCore_customData.isEmpty()) {
			output.store(SOPHISTICATED_CORE_DATA_KEY, CompoundTag.CODEC, sophisticatedCore_customData);
		}
	}

	@Inject(
			method = "load(Lnet/minecraft/world/level/storage/ValueInput;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V"
			)
	)
	private void sophisticatedCore_loadCustomData(ValueInput input, CallbackInfo ci) {
		sophisticatedCore_customData = input.read(SOPHISTICATED_CORE_DATA_KEY, CompoundTag.CODEC).orElse(null);
	}
}
