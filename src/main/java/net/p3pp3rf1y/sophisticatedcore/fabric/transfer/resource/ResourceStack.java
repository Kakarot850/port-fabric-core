/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 *
 * Port note (sophisticatedcore Fabric): copied from NeoForge 26.1.2.53-beta net.neoforged.neoforge.transfer.resource.ResourceStack, package renamed to
 * net.p3pp3rf1y.sophisticatedcore.fabric.transfer; see tools/gen_transfer.py for the edits.
 */

package net.p3pp3rf1y.sophisticatedcore.fabric.transfer.resource;

import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.ResourceHandlerUtil;

/**
 * Creates a resource stack from a given {@link Resource resource} and {@code amount}.
 *
 * @param resource The resource to wrap the stack around.
 * @param amount   The amount of the resource the stack is holding.
 */
public record ResourceStack<T extends Resource>(T resource, int amount) {
    /**
     * Checks if the resource stack is empty, meaning that the amount is zero
     * or that the resource is {@link Resource#isEmpty() empty}.
     *
     * @return {@code true} if empty
     */
    public boolean isEmpty() {
        return ResourceHandlerUtil.isEmpty(resource(), amount());
    }

    @Override
    public String toString() {
        return amount + "x " + resource;
    }
}
