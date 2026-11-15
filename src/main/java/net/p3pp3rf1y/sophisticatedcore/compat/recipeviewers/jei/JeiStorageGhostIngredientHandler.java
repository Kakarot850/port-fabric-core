package net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.fabric.constants.FabricTypes;
import mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.p3pp3rf1y.sophisticatedcore.fabric.capabilities.Capabilities;
import net.p3pp3rf1y.sophisticatedcore.fabric.client.network.ClientPacketDistributor;
import net.p3pp3rf1y.sophisticatedcore.fabric.fluids.FluidStack;
import net.p3pp3rf1y.sophisticatedcore.fabric.fluids.FluidType;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.access.ItemAccess;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.common.gui.IFilterSlot;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.common.SetGhostSlotPayload;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.util.CapabilityHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class JeiStorageGhostIngredientHandler<S extends StorageScreenBase<?>> implements IGhostIngredientHandler<S> {
	@Override
	public <I> List<Target<I>> getTargetsTyped(S gui, ITypedIngredient<I> ingredient, boolean doStart) {
		List<Target<I>> targets = new ArrayList<>();
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			StorageContainerMenuBase<?> container = gui.getMenu();
			ingredient.getItemStack().ifPresent(ghostStack -> {
				FluidStack fluidStack = CapabilityHelper.getFromCapability(ItemAccess.forStack(ghostStack), Capabilities.Fluid.ITEM,
						fluidHandler -> fluidHandler.size() > 0 ? fluidHandler.getResource(0).toStack(FluidType.BUCKET_VOLUME) : FluidStack.EMPTY,
						FluidStack.EMPTY);
				if (!fluidStack.isEmpty()) {
					gui.getUpgradeSettingsControl().getOpenTab().filter(tab -> tab instanceof PumpUpgradeTab.Advanced).map(PumpUpgradeTab.Advanced.class::cast)
							.ifPresent(pumpUpgradeTab -> addFluidTargets(pumpUpgradeTab, fluidStack, targets));
				}
				container.getOpenContainer().ifPresent(c -> c.getSlots().forEach(s -> {
					if (s instanceof IFilterSlot && s.mayPlace(ghostStack)) {
						targets.add(new Target<>() {
							@Override
							public Rect2i getArea() {
								return new Rect2i(gui.sophisticatedCore_getGuiLeft() + s.x, gui.sophisticatedCore_getGuiTop() + s.y, 17, 17);
							}

							@Override
							public void accept(I i) {
								ClientPacketDistributor.sendToServer(new SetGhostSlotPayload(ghostStack, s.index));
							}
						});
					}
				}));
			});
		} else if (ingredient.getType() == FabricTypes.FLUID_STACK) {
			Optional<FluidStack> ghostFluid = toFluidStack(FabricTypes.FLUID_STACK.castIngredient(ingredient.getIngredient()));
			ghostFluid.ifPresent(fluidStack -> {
				gui.getUpgradeSettingsControl().getOpenTab().filter(tab -> tab instanceof PumpUpgradeTab.Advanced).map(PumpUpgradeTab.Advanced.class::cast)
						.ifPresent(pumpUpgradeTab -> addFluidTargets(pumpUpgradeTab, fluidStack, targets));
				gui.getUpgradeSettingsControl().getOpenTab().filter(tab -> tab instanceof VoidUpgradeTab).map(VoidUpgradeTab.class::cast)
						.ifPresent(voidUpgradeTab -> addVoidFluidTargets(gui, voidUpgradeTab, fluidStack, targets));
			});
		}
		return targets;
	}

	private static Optional<FluidStack> toFluidStack(Optional<IJeiFluidIngredient> jeiFluid) {
		return jeiFluid.map(fluid -> {
			long amount = fluid.getAmount();
			int mb = amount <= 0 ? FluidType.BUCKET_VOLUME : Math.max(1, (int) (amount / FluidType.DROPLETS_PER_MB));
			return FluidStack.fromVariant(fluid.getFluidVariant(), mb);
		}).filter(stack -> !stack.isEmpty());
	}

	private <I> void addFluidTargets(PumpUpgradeTab.Advanced pumpUpgradeTab, FluidStack ghostFluid, List<Target<I>> targets) {
		List<Position> slotTopLeftPositions = pumpUpgradeTab.getFluidFilterControl().getSlotTopLeftPositions();
		AtomicInteger slot = new AtomicInteger();
		for (slot.set(0); slot.get() < slotTopLeftPositions.size(); slot.incrementAndGet()) {
			Position position = slotTopLeftPositions.get(slot.get());
			targets.add(new Target<>() {
				private final int slotIndex = slot.get();
				@Override
				public Rect2i getArea() {
					return new Rect2i(position.x(), position.y(), 17, 17);
				}

				@Override
				public void accept(I i) {
					pumpUpgradeTab.getFluidFilterControl().setFluid(slotIndex, ghostFluid);
				}
			});
		}
	}

	private <I> void addVoidFluidTargets(S gui, VoidUpgradeTab voidUpgradeTab, FluidStack ghostFluid, List<Target<I>> targets) {
		gui.getMenu().getOpenContainer().ifPresent(container -> container.getSlots().forEach(slot -> {
			if (slot instanceof FilterLogicContainer.FilterLogicSlot filterSlot) {
				int filterSlotIndex = filterSlot.slot;
				targets.add(new Target<>() {
					@Override
					public Rect2i getArea() {
						return new Rect2i(gui.sophisticatedCore_getGuiLeft() + slot.x, gui.sophisticatedCore_getGuiTop() + slot.y, 17, 17);
					}

					@Override
					public void accept(I i) {
						voidUpgradeTab.setFluidFilter(filterSlotIndex, ghostFluid);
					}
				});
			}
		}));
	}

	@Override
	public void onComplete() {
		// noop
	}

	@Override
	public <I> boolean quickMove(S gui, ITypedIngredient<I> ingredient) {
		if (ingredient.getType() != VanillaTypes.ITEM_STACK) {
			return false;
		}

		return ingredient.getItemStack().map(stack -> {
			Optional<Slot> target = gui.getMenu().getOpenContainer().flatMap(container -> container.getSlots().stream()
					.filter(slot -> slot instanceof IFilterSlot && slot.isActive() && slot.getItem().isEmpty() && slot.mayPlace(stack)).findFirst());
			target.ifPresent(slot -> ClientPacketDistributor.sendToServer(new SetGhostSlotPayload(stack, slot.index)));
			return target.isPresent();
		}).orElse(false);
	}
}
