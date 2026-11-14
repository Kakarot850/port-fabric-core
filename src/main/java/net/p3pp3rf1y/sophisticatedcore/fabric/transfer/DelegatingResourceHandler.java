/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 *
 * Port note (sophisticatedcore Fabric): copied from NeoForge 26.1.2.53-beta net.neoforged.neoforge.transfer.DelegatingResourceHandler, package renamed to
 * net.p3pp3rf1y.sophisticatedcore.fabric.transfer; see tools/gen_transfer.py for the edits.
 */

package net.p3pp3rf1y.sophisticatedcore.fabric.transfer;

import java.util.Objects;
import java.util.function.Supplier;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.resource.Resource;
import net.p3pp3rf1y.sophisticatedcore.fabric.transfer.transaction.TransactionContext;

/**
 * A resource handler that delegates all calls to another handler.
 */
public class DelegatingResourceHandler<T extends Resource> implements ResourceHandler<T> {
    protected final Supplier<ResourceHandler<T>> delegate;

    public DelegatingResourceHandler(ResourceHandler<T> delegate) {
        Objects.requireNonNull(delegate);
        this.delegate = () -> delegate;
    }

    public DelegatingResourceHandler(Supplier<ResourceHandler<T>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        return getDelegate().size();
    }

    /**
     * Converts the external index to the internal index to use for the delegated-to handler.
     */
    protected int convertIndex(int index) {
        Objects.checkIndex(index, size());
        return index;
    }

    @Override
    public T getResource(int index) {
        return getDelegate().getResource(convertIndex(index));
    }

    @Override
    public long getAmountAsLong(int index) {
        return getDelegate().getAmountAsLong(convertIndex(index));
    }

    @Override
    public long getCapacityAsLong(int index, T resource) {
        return getDelegate().getCapacityAsLong(convertIndex(index), resource);
    }

    @Override
    public boolean isValid(int index, T resource) {
        if (resource.isEmpty()) return true;
        return getDelegate().isValid(convertIndex(index), resource);
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        return getDelegate().insert(convertIndex(index), resource, amount, transaction);
    }

    @Override
    public int insert(T resource, int amount, TransactionContext transaction) {
        return getDelegate().insert(resource, amount, transaction);
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        return getDelegate().extract(convertIndex(index), resource, amount, transaction);
    }

    @Override
    public int extract(T resource, int amount, TransactionContext transaction) {
        return getDelegate().extract(resource, amount, transaction);
    }

    public ResourceHandler<T> getDelegate() {
        return delegate.get();
    }
}
