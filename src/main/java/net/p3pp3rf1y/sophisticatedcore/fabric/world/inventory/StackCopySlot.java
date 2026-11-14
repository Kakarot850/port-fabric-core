/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 *
 * Port note (sophisticatedcore Fabric): copied from NeoForge 26.1.2.53-beta
 * net.neoforged.neoforge.world.inventory.StackCopySlot; only the package and javadoc links changed.
 */

package net.p3pp3rf1y.sophisticatedcore.fabric.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Slot that reads and writes its stack through {@link #getStackCopy()} / {@link #setStackCopy(ItemStack)} instead
 * of a {@link Container}, for handlers that only hand out copies (e.g.
 * {@code net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ResourceHandlerSlot}).
 *
 * <p>Vanilla code may mutate the returned stack and then call {@link #setChanged()}; this slot remembers the stack it
 * last returned and writes it back if it changed.
 */
public abstract class StackCopySlot extends Slot {
    private static final Container emptyInventory = new SimpleContainer(0);

    @Nullable
    private ItemStack cachedReturnedStack = null;

    public StackCopySlot(int slot, int x, int y) {
        super(emptyInventory, slot, x, y);
    }

    /**
     * {@return a copy of the stack in this slot}
     */
    protected abstract ItemStack getStackCopy();

    /**
     * Sets the stack in this slot.
     */
    protected abstract void setStackCopy(ItemStack stack);

    @Override
    public final ItemStack getItem() {
        return cachedReturnedStack = getStackCopy();
    }

    @Override
    public final void set(ItemStack stack) {
        setStackCopy(stack);
        cachedReturnedStack = stack;
    }

    @Override
    public final void setChanged() {
        // Verify that the stack has actually changed before setting it.
        // Vanilla menu logic (like AbstractContainerMenu#moveItemStackTo) often already sets the stack through Slot#setByPlayer.
        // This is done to prevent slot change logic from running multiple times when not necessary.
        if (cachedReturnedStack != null && !ItemStack.matches(cachedReturnedStack, getStackCopy())) {
            set(cachedReturnedStack);
        }
    }

    @Override
    public ItemStack remove(int amount) {
        ItemStack stack = getStackCopy().copy();
        ItemStack ret = stack.split(amount);
        set(stack);
        cachedReturnedStack = null;
        return ret;
    }
}
