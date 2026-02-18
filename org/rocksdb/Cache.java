/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public abstract class Cache
extends RocksObject {
    protected Cache(long l) {
        super(l);
    }

    public long getUsage() {
        assert (this.isOwningHandle());
        return Cache.getUsage(this.nativeHandle_);
    }

    public long getPinnedUsage() {
        assert (this.isOwningHandle());
        return Cache.getPinnedUsage(this.nativeHandle_);
    }

    private static native long getUsage(long var0);

    private static native long getPinnedUsage(long var0);
}

