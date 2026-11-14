package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.DataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * NeoForge 26.1.2 makes {@code SavedDataType#dataFixType} nullable (mod saved data has no DFU fixes) and skips the
 * data fixer for such types when reading from disk. Vanilla would call {@code update} on {@code null}. Same skip here,
 * for {@code SavedDataType}s created with {@code VanillaStatics#savedDataType} (NeoForge's 3-argument constructor).
 */
@Mixin(SavedDataStorage.class)
public class SavedDataStorageMixin {
	@WrapOperation(method = "readTagFromDisk", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/util/datafix/DataFixTypes;update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/nbt/CompoundTag;II)Lnet/minecraft/nbt/CompoundTag;"))
	private CompoundTag sophisticatedCore_skipNullDataFixType(DataFixTypes type, DataFixer fixerUpper, CompoundTag tag, int version, int newVersion,
			Operation<CompoundTag> original) {
		return type == null ? tag : original.call(type, fixerUpper, tag, version, newVersion);
	}
}
