/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public abstract class AbstractNativeReference
implements AutoCloseable {
    protected abstract boolean isOwningHandle();

    @Override
    public abstract void close();
}

