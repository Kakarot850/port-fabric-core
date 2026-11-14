package net.p3pp3rf1y.sophisticatedcore.init;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.fabric.fluids.BaseFlowingFluid;
import net.p3pp3rf1y.sophisticatedcore.fabric.registries.DeferredRegister;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class ModFluids {
	private ModFluids() {
	}

	private static BaseFlowingFluid.Properties fluidProperties() {
		return new BaseFlowingFluid.Properties(XP_STILL, XP_FLOWING).bucket(XP_BUCKET);
	}

	public static final Identifier EXPERIENCE_TAG_NAME = Identifier.fromNamespaceAndPath("c", "experience");

	public static final TagKey<Fluid> EXPERIENCE_TAG = TagKey.create(Registries.FLUID, EXPERIENCE_TAG_NAME);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, SophisticatedCore.MOD_ID);

	public static final Supplier<FlowingFluid> XP_STILL = FLUIDS.register("xp_still", () -> new BaseFlowingFluid.Source(fluidProperties()));

	public static final Supplier<FlowingFluid> XP_FLOWING = FLUIDS.register("xp_flowing", () -> new BaseFlowingFluid.Flowing(fluidProperties()));

	/**
	 * NeoForge registers a {@code FluidType} "experience" ({@code lightLevel(10).density(800).viscosity(1500)}) as
	 * {@code XP_FLUID_TYPE}. Fabric has no FluidType registry, so the same properties go through the Transfer API.
	 * Density 800 (heavier than air) is Fabric's default.
	 */
	public static final FluidVariantAttributeHandler XP_FLUID_ATTRIBUTES = new FluidVariantAttributeHandler() {
		@Override
		public Component getName(FluidVariant fluidVariant) {
			// NeoForge FluidType#getDescriptionId: "fluid_type.<namespace>.<path>"
			return Component.translatable("fluid_type." + SophisticatedCore.MOD_ID + ".experience");
		}

		@Override
		public int getLightEmission(FluidVariant fluidVariant) {
			return 10;
		}

		@Override
		public int getViscosity(FluidVariant fluidVariant, @Nullable Level level) {
			return 1500; // Fabric's FluidConstants use NeoForge's scale (water 1000, lava 6000)
		}
	};

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SophisticatedCore.MOD_ID);
	public static final Supplier<Item> XP_BUCKET = ITEMS.registerItem("xp_bucket", properties -> new BucketItem(XP_STILL.get(), properties.stacksTo(1)));

	public static void registerHandlers() {
		FLUIDS.register();
		ITEMS.register();
		FluidVariantAttributes.register(XP_STILL.get(), XP_FLUID_ATTRIBUTES);
		FluidVariantAttributes.register(XP_FLOWING.get(), XP_FLUID_ATTRIBUTES);
	}
}
