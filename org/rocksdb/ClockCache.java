/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Cache;

public class ClockCache
extends Cache {
    public ClockCache(long l) {
        super(ClockCache.newClockCache(l, -1, false));
    }

    public ClockCache(long l, int n) {
        super(ClockCache.newClockCache(l, n, false));
    }

    public ClockCache(long l, int n, boolean bl) {
        super(ClockCache.newClockCache(l, n, bl));
    }

    private static native long newClockCache(long var0, int var2, boolean var3);

    @Override
    protected final void disposeInternal(long l) {
        ClockCache.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

