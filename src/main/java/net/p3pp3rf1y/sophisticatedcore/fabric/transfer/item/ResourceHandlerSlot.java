/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 *
 * Port note (sophisticatedcore Fabric): copied from NeoForge 26.1.2.53-beta net.neoforged.neoforge.transfer.item.ResourceHandlerSlot, package renamed to
 * net.p3pp3rf1y.sophisticatedcore.fabric.transfer; see tools/gen_transfer.py for the edits.
 */

package net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.IndexModifier;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.ResourceHandler;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.StacksResourceHandler;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.Transaction;
import net.p3pp3rf1y.sophisticatedcore.fabric.world.inventory.StackCopySlot;

/**
 * Base implementation for a {@link Slot} backed by a {@code ResourceHandler<ItemResource>}.
 * Requires the handler to expose a {@linkplain IndexModifier direct mutation function},
 * such as {@link StacksResourceHandler#set}.
 */
public class ResourceHandlerSlot extends StackCopySlot {
    private final ResourceHandler<ItemResource> handler;
    private final IndexModifier<ItemResource> slotModifier;

    public ResourceHandlerSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> slotModifier, int handlerSlot, int xPosition, int yPosition) {
        super(handlerSlot, xPosition, yPosition);
        this.handler = handler;
        this.slotModifier = slotModifier;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.isEmpty())
            return false;
        // Use isValid as a reasonable estimate.
        // We can't try to insert as we don't want to check the current contents to allow swapping.
        // This method is left for mods to override if this is not sufficient.
        return handler.isValid(this.sophisticatedCore_getSlotIndex(), ItemResource.of(stack));
    }

    @Override
    protected ItemStack getStackCopy() {
        return handler.getResource(this.sophisticatedCore_getSlotIndex()).toStack(handler.getAmountAsInt(this.sophisticatedCore_getSlotIndex()));
    }

    @Override
    protected void setStackCopy(ItemStack stack) {
        slotModifier.set(this.sophisticatedCore_getSlotIndex(), ItemResource.of(stack), stack.getCount());
    }

    @Override
    public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {}

    @Override
    public int getMaxStackSize() {
        return handler.getCapacityAsInt(this.sophisticatedCore_getSlotIndex(), ItemResource.EMPTY);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return handler.getCapacityAsInt(this.sophisticatedCore_getSlotIndex(), ItemResource.of(stack));
    }

    @Override
    public boolean mayPickup(Player player) {
        var resource = handler.getResource(this.sophisticatedCore_getSlotIndex());
        if (resource.isEmpty()) {
            return false;
        }
        try (var tx = Transaction.openRoot()) {
            // Simulated extraction
            return handler.extract(this.sophisticatedCore_getSlotIndex(), resource, 1, tx) == 1;
        }
    }

    public ResourceHandler<ItemResource> getResourceHandler() {
        return handler;
    }

    @Override
    public boolean sophisticatedCore_isSameInventory(Slot other) {
        return other instanceof ResourceHandlerSlot rhs && rhs.handler == this.handler;
    }
}
