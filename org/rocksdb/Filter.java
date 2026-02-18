/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public abstract class Filter
extends RocksObject {
    protected Filter(long l) {
        super(l);
    }

    @Override
    protected void disposeInternal() {
        this.disposeInternal(this.nativeHandle_);
    }

    @Override
    protected final void disposeInternal(long l) {
        Filter.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

