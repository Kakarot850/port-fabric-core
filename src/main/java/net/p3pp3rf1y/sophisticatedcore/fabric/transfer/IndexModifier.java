/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 *
 * Port note (sophisticatedcore Fabric): copied from NeoForge 26.1.2.53-beta net.neoforged.neoforge.transfer.IndexModifier, package renamed to
 * net.p3pp3rf1y.sophisticatedcore.fabric.transfer; see tools/gen_transfer.py for the edits.
 */

package net.p3pp3rf1y.sophisticatedcore.fabric.transfer;

import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.resource.Resource;

/**
 * Represents a function to directly mutate the resource and amount at a specific index of a {@link ResourceHandler}.
 */
@FunctionalInterface
public interface IndexModifier<T extends Resource> {
    /**
     * Overrides the resource and amount at the given index.
     *
     * @param index    The index to set the resource at.
     * @param resource The resource to set.
     * @param amount   The amount of the resource to set.
     */
    void set(int index, T resource, int amount);
}
