package net.p3pp3rf1y.sophisticatedcore.fabric.fluids;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fabric facade for the parts of NeoForge's {@code FluidType} that Sophisticated Core and Storage use
 * ({@link #BUCKET_VOLUME}, {@link #isLighterThanAir()}, {@link #getLightLevel(FluidStack)},
 * {@link #getDescriptionId(FluidStack)}, {@link #getDescription(FluidStack)}, and level placement/vaporization
 * helpers). Fabric has no FluidType registry, so the attributes come from the Transfer API's
 * {@link FluidVariantAttributes}. There is one instance per source fluid, so a source and its flowing form share a
 * type and identity comparison works as with NeoForge's types.
 */
public final class FluidType {
	/** Millibuckets per bucket; the unit used by {@link FluidStack} amounts. */
	public static final int BUCKET_VOLUME = 1000;
	/** Fabric Transfer API droplets per millibucket (81). Use only when crossing into the Transfer API. */
	public static final long DROPLETS_PER_MB = FluidConstants.BUCKET / BUCKET_VOLUME;

	private static final Map<Fluid, FluidType> TYPES = new ConcurrentHashMap<>();

	private final Fluid fluid;

	private FluidType(Fluid fluid) {
		this.fluid = fluid;
	}

	public static FluidType of(Fluid fluid) {
		Fluid source = fluid instanceof FlowingFluid flowing && !fluid.isSource(fluid.defaultFluidState()) ? flowing.getSource() : fluid;
		return TYPES.computeIfAbsent(source, FluidType::new);
	}

	public boolean isLighterThanAir() {
		return FluidVariantAttributes.isLighterThanAir(FluidVariant.of(fluid));
	}

	public int getLightLevel() {
		return FluidVariantAttributes.getLuminance(FluidVariant.of(fluid));
	}

	public int getLightLevel(FluidStack stack) {
		return stack.isEmpty() ? getLightLevel() : FluidVariantAttributes.getLuminance(stack.toVariant());
	}

	public Component getDescription() {
		return FluidVariantAttributes.getName(FluidVariant.of(fluid));
	}

	public Component getDescription(FluidStack stack) {
		return stack.isEmpty() ? getDescription() : FluidVariantAttributes.getName(stack.toVariant());
	}

	public String getDescriptionId() {
		return descriptionIdOf(getDescription());
	}

	/**
	 * NeoForge returns the type's translation key. Fabric exposes names as components, so this returns the key of a
	 * translatable name and otherwise the literal text.
	 */
	public String getDescriptionId(FluidStack stack) {
		return descriptionIdOf(getDescription(stack));
	}

	public BlockState getBlockForFluidState(BlockGetter level, BlockPos pos, FluidState state) {
		return state.createLegacyBlock();
	}

	public FluidState getStateForPlacement(BlockGetter level, BlockPos pos, FluidStack stack) {
		return stack.getFluid().defaultFluidState();
	}

	public boolean canBePlacedInLevel(BlockGetter level, BlockPos pos, FluidState state) {
		return !getBlockForFluidState(level, pos, state).isAir();
	}

	public boolean canBePlacedInLevel(BlockGetter level, BlockPos pos, FluidStack stack) {
		return canBePlacedInLevel(level, pos, getStateForPlacement(level, pos, stack));
	}

	public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack) {
		return level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)
				&& getStateForPlacement(level, pos, stack).is(FluidTags.WATER);
	}

	public void onVaporize(@Nullable LivingEntity entity, Level level, BlockPos pos, FluidStack stack) {
		level.playSound(entity, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
				2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
		for (int l = 0; l < 8; ++l) {
			level.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE,
					(double) pos.getX() + Math.random(),
					(double) pos.getY() + Math.random(),
					(double) pos.getZ() + Math.random(),
					0.0D, 0.0D, 0.0D);
		}
	}

	private static String descriptionIdOf(Component name) {
		return name.getContents() instanceof TranslatableContents translatable ? translatable.getKey() : name.getString();
	}
}
