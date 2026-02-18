/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Cache;

public class LRUCache
extends Cache {
    public LRUCache(long l) {
        this(l, -1, false, 0.0, 0.0);
    }

    public LRUCache(long l, int n) {
        super(LRUCache.newLRUCache(l, n, false, 0.0, 0.0));
    }

    public LRUCache(long l, int n, boolean bl) {
        super(LRUCache.newLRUCache(l, n, bl, 0.0, 0.0));
    }

    public LRUCache(long l, int n, boolean bl, double d) {
        super(LRUCache.newLRUCache(l, n, bl, d, 0.0));
    }

    public LRUCache(long l, int n, boolean bl, double d, double d2) {
        super(LRUCache.newLRUCache(l, n, bl, d, d2));
    }

    private static native long newLRUCache(long var0, int var2, boolean var3, double var4, double var6);

    @Override
    protected final void disposeInternal(long l) {
        LRUCache.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

