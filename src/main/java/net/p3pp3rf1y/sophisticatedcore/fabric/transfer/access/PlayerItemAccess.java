/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 *
 * Port note (sophisticatedcore Fabric): copied from NeoForge 26.1.2.53-beta net.neoforged.neoforge.transfer.access.PlayerItemAccess, package renamed to
 * net.p3pp3rf1y.sophisticatedcore.fabric.transfer; see tools/gen_transfer.py for the edits.
 */

package net.p3pp3rf1y.sophisticatedcore.fabric.transfer.access;

import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.ResourceHandler;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.PlayerInventoryWrapper;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.TransactionContext;

class PlayerItemAccess implements ItemAccess {
    private final PlayerInventoryWrapper inventoryWrapper;
    private final ResourceHandler<ItemResource> slot;

    public PlayerItemAccess(PlayerInventoryWrapper inventoryWrapper, ResourceHandler<ItemResource> slot) {
        this.inventoryWrapper = inventoryWrapper;
        this.slot = slot;
    }

    @Override
    public ItemResource getResource() {
        return slot.getResource(0);
    }

    @Override
    public int getAmount() {
        return slot.getAmountAsInt(0);
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        int inserted = slot.insert(0, resource, amount, transaction);
        if (amount > inserted) {
            inventoryWrapper.placeItemBackInInventory(resource, amount - inserted, transaction);
        }
        // Any leftover is dropped, so the full amount can always be accepted
        return amount;
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        return slot.extract(0, resource, amount, transaction);
    }
}
