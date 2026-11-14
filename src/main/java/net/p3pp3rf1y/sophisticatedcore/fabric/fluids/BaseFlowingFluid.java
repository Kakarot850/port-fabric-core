package net.p3pp3rf1y.sophisticatedcore.fabric.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Fabric stand-in for NeoForge's {@code net.neoforged.neoforge.fluids.BaseFlowingFluid}, adapted from NeoForge
 * 26.1.2.53-beta (LGPL-2.1). Behaviour matches NeoForge's class except for the parts that need a NeoForge
 * {@code FluidType}, which Fabric doesn't have:
 * <ul>
 * <li>No {@code getFluidType()}. Fluid attributes (name, light, viscosity) are registered with the Fabric Transfer API's
 * {@code FluidVariantAttributes} instead.</li>
 * <li>No {@code canConvertToSource(FluidState, ServerLevel, BlockPos)} override. NeoForge delegates it to
 * {@code FluidType#canConvertToSource}, which defaults to {@code false}, the same as vanilla
 * {@link #canConvertToSource(ServerLevel)} below.</li>
 * <li>No {@code getPickupSound()} override. NeoForge returns the FluidType's {@code BUCKET_FILL} sound, which is unset by
 * default, so this falls back to vanilla's empty default.</li>
 * </ul>
 */
public abstract class BaseFlowingFluid extends FlowingFluid {
	private final Supplier<? extends Fluid> flowing;
	private final Supplier<? extends Fluid> still;
	@Nullable
	private final Supplier<? extends Item> bucket;
	@Nullable
	private final Supplier<? extends LiquidBlock> block;
	private final int slopeFindDistance;
	private final int levelDecreasePerBlock;
	private final float explosionResistance;
	private final int tickRate;

	protected BaseFlowingFluid(Properties properties) {
		this.flowing = properties.flowing;
		this.still = properties.still;
		this.bucket = properties.bucket;
		this.block = properties.block;
		this.slopeFindDistance = properties.slopeFindDistance;
		this.levelDecreasePerBlock = properties.levelDecreasePerBlock;
		this.explosionResistance = properties.explosionResistance;
		this.tickRate = properties.tickRate;
	}

	@Override
	public Fluid getFlowing() {
		return flowing.get();
	}

	@Override
	public Fluid getSource() {
		return still.get();
	}

	@Override
	protected boolean canConvertToSource(ServerLevel level) {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
		Block.dropResources(state, level, pos, blockEntity);
	}

	@Override
	protected int getSlopeFindDistance(LevelReader level) {
		return slopeFindDistance;
	}

	@Override
	protected int getDropOff(LevelReader level) {
		return levelDecreasePerBlock;
	}

	@Override
	public Item getBucket() {
		return bucket != null ? bucket.get() : Items.AIR;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
		// Based on the water implementation (as in NeoForge); override for fluids that shouldn't behave like water.
		return direction == Direction.DOWN && !isSame(fluid);
	}

	@Override
	public int getTickDelay(LevelReader level) {
		return tickRate;
	}

	@Override
	protected float getExplosionResistance() {
		return explosionResistance;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		if (block != null) {
			return block.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
		}
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == still.get() || fluid == flowing.get();
	}

	public static class Flowing extends BaseFlowingFluid {
		public Flowing(Properties properties) {
			super(properties);
			registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
		}

		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Source extends BaseFlowingFluid {
		public Source(Properties properties) {
			super(properties);
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Properties {
		private final Supplier<? extends Fluid> still;
		private final Supplier<? extends Fluid> flowing;
		@Nullable
		private Supplier<? extends Item> bucket;
		@Nullable
		private Supplier<? extends LiquidBlock> block;
		private int slopeFindDistance = 4;
		private int levelDecreasePerBlock = 1;
		private float explosionResistance = 1;
		private int tickRate = 5;

		/**
		 * NeoForge's constructor also takes a {@code Supplier<FluidType>}; see the class javadoc.
		 */
		public Properties(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing) {
			this.still = still;
			this.flowing = flowing;
		}

		public Properties bucket(Supplier<? extends Item> bucket) {
			this.bucket = bucket;
			return this;
		}

		public Properties block(Supplier<? extends LiquidBlock> block) {
			this.block = block;
			return this;
		}

		public Properties slopeFindDistance(int slopeFindDistance) {
			this.slopeFindDistance = slopeFindDistance;
			return this;
		}

		public Properties levelDecreasePerBlock(int levelDecreasePerBlock) {
			this.levelDecreasePerBlock = levelDecreasePerBlock;
			return this;
		}

		public Properties explosionResistance(float explosionResistance) {
			this.explosionResistance = explosionResistance;
			return this;
		}

		public Properties tickRate(int tickRate) {
			this.tickRate = tickRate;
			return this;
		}
	}
}
