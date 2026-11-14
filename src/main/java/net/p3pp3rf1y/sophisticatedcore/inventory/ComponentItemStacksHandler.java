package net.p3pp3rf1y.sophisticatedcore.inventory;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.IndexModifier;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemStacksResourceHandler;

public class ComponentItemStacksHandler extends ItemStacksResourceHandler implements IndexModifier<ItemResource> {
	// Port: NeoForge's MutableDataComponentHolder; the only holders passed are upgrade ItemStacks
	protected final ItemStack parent;
	protected final DataComponentType<ItemContainerContents> component;

	public ComponentItemStacksHandler(ItemStack parent, DataComponentType<ItemContainerContents> component, int size) {
		super(size);
		this.parent = parent;
		this.component = component;
		getContents().copyInto(stacks);
	}

	protected ItemContainerContents getContents() {
		return parent.getOrDefault(component, ItemContainerContents.EMPTY);
	}

	@Override
	protected void onContentsChanged(int index, ItemStack previousContents) {
		super.onContentsChanged(index, previousContents);
		updateContents(stacks.get(index), index);
	}

	protected void updateContents(ItemStack stack, int slot) {
		stacks.set(slot, stack);
		parent.set(component, ItemContainerContents.fromItems(stacks));
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		ItemStack stack = resource.toStack();
		// Port: NeoForge IItemExtension#canFitInsideContainerItems(ItemStack) default
		return stack.getItem().canFitInsideContainerItems();
	}

	public ItemStack getStackInSlot(int slot) {
		return stacks.get(slot);
	}
}
