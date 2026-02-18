/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Cache;

public class HyperClockCache
extends Cache {
    public HyperClockCache(long l, long l2, int n, boolean bl) {
        super(HyperClockCache.newHyperClockCache(l, l2, n, bl));
    }

    @Override
    protected void disposeInternal(long l) {
        HyperClockCache.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native long newHyperClockCache(long var0, long var2, int var4, boolean var5);
}

