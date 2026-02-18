/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.AbstractImmutableNativeReference;

public abstract class RocksObject
extends AbstractImmutableNativeReference {
    protected final long nativeHandle_;

    protected RocksObject(long l) {
        super(true);
        this.nativeHandle_ = l;
    }

    @Override
    protected void disposeInternal() {
        this.disposeInternal(this.nativeHandle_);
    }

    protected abstract void disposeInternal(long var1);

    public long getNativeHandle() {
        return this.nativeHandle_;
    }
}

