package net.p3pp3rf1y.sophisticatedcore.mixin.common;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedcore.extensions.block.entity.SophisticatedBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements SophisticatedBlockEntity {
	@Unique
	private boolean sophisticatedCore_loaded = false;

	@Override
	public boolean sophisticatedCore_isLoaded() {
		return sophisticatedCore_loaded;
	}

	@Override
	public void sophisticatedCore_setLoaded(boolean loaded) {
		this.sophisticatedCore_loaded = loaded;
	}
}
