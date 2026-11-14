/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 *
 * Port note (sophisticatedcore Fabric): copied from NeoForge 26.1.2.53-beta net.neoforged.neoforge.transfer.TransferPreconditions, package renamed to
 * net.p3pp3rf1y.sophisticatedcore.fabric.transfer; see tools/gen_transfer.py for the edits.
 */

package net.p3pp3rf1y.sophisticatedcore.fabric.transfer;

import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.resource.Resource;

/**
 * Precondition checks useful for implementing {@link ResourceHandler}.
 */
public class TransferPreconditions {
    private TransferPreconditions() {}

    /**
     * Ensures the resource is non-empty, throws otherwise.
     *
     * @throws IllegalArgumentException when resource is empty.
     */
    public static void checkNonEmpty(Resource resource) {
        if (resource.isEmpty()) {
            throw new IllegalArgumentException("Expected resource to be non-empty: " + resource);
        }
    }

    /**
     * Ensures the value is non-negative, throws otherwise.
     *
     * @throws IllegalArgumentException when value is negative.
     */
    public static void checkNonNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Expected value to be non-negative: " + value);
        }
    }

    /**
     * Ensures the resource is non-empty and the value is non-negative, throws otherwise.
     *
     * @throws IllegalArgumentException when resource is empty or value is negative.
     */
    public static void checkNonEmptyNonNegative(Resource resource, int value) {
        checkNonEmpty(resource);
        checkNonNegative(value);
    }
}
