/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 *
 * Port note (sophisticatedcore Fabric): copied from NeoForge 26.1.2.53-beta net.neoforged.neoforge.transfer.access.HandlerItemAccess, package renamed to
 * net.p3pp3rf1y.sophisticatedcore.fabric.transfer; see tools/gen_transfer.py for the edits.
 */

package net.p3pp3rf1y.sophisticatedcore.fabric.transfer.access;

import java.util.Objects;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.ResourceHandler;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.TransferPreconditions;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.item.ItemResource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.TransactionContext;

public class HandlerItemAccess implements ItemAccess {
    protected final ResourceHandler<ItemResource> handler;
    protected final int index;

    public HandlerItemAccess(ResourceHandler<ItemResource> handler, int index) {
        Objects.checkIndex(index, handler.size());
        this.handler = handler;
        this.index = index;
    }

    @Override
    public ItemResource getResource() {
        return handler.getResource(index);
    }

    @Override
    public int getAmount() {
        return handler.getAmountAsInt(index);
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        int inserted = handler.insert(index, resource, amount, transaction);
        if (inserted < amount) {
            // Insert any leftover into the rest of the handler
            inserted += handler.insert(resource, amount - inserted, transaction);
        }
        return inserted;
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        return handler.extract(index, resource, amount, transaction);
    }
}
