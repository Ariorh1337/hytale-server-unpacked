/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.List;
import org.rocksdb.AbstractImmutableNativeReference;

public abstract class RocksCallbackObject
extends AbstractImmutableNativeReference {
    protected final long nativeHandle_;

    protected RocksCallbackObject(long ... lArray) {
        super(true);
        this.nativeHandle_ = this.initializeNative(lArray);
    }

    static long[] toNativeHandleList(List<? extends RocksCallbackObject> list) {
        if (list == null) {
            return new long[0];
        }
        int n = list.size();
        long[] lArray = new long[n];
        for (int i = 0; i < n; ++i) {
            lArray[i] = list.get((int)i).nativeHandle_;
        }
        return lArray;
    }

    protected abstract long initializeNative(long ... var1);

    @Override
    protected void disposeInternal() {
        RocksCallbackObject.disposeInternal(this.nativeHandle_);
    }

    private static native void disposeInternal(long var0);
}

