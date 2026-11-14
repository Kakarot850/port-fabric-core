package net.p3pp3rf1y.sophisticatedcore.util;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.ResourceHandler;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.access.ItemAccess;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.VanillaContainerWrapper;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.TransactionContext;

public class MutableStackItemAccess implements ItemAccess {
	private final ResourceHandler<ItemResource> wrapper;
	private final SimpleContainer container;

	public MutableStackItemAccess(ItemStack stack) {
		container = new SimpleContainer(stack) {
			public void setItem(int slot, ItemStack stack, boolean performSideEffects) {
				getItems().set(slot, stack);
			}
		};
		this.wrapper = VanillaContainerWrapper.of(container);
	}

	public ItemResource getResource() {
		return wrapper.getResource(0);
	}

	public int getAmount() {
		return wrapper.getAmountAsInt(0);
	}

	public ItemStack getStack() {
		return container.getItem(0);
	}

	public int insert(ItemResource resource, int amount, TransactionContext transaction) {
		return wrapper.insert(resource, amount, transaction);
	}

	public int extract(ItemResource resource, int amount, TransactionContext transaction) {
		return wrapper.extract(resource, amount, transaction);
	}
}
