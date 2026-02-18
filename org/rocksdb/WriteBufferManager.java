/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Cache;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksObject;

public class WriteBufferManager
extends RocksObject {
    private final boolean allowStall_;

    public WriteBufferManager(long l, Cache cache, boolean bl) {
        super(WriteBufferManager.newWriteBufferManagerInstance(l, cache.nativeHandle_, bl));
        this.allowStall_ = bl;
    }

    public WriteBufferManager(long l, Cache cache) {
        this(l, cache, false);
    }

    public boolean allowStall() {
        return this.allowStall_;
    }

    private static long newWriteBufferManagerInstance(long l, long l2, boolean bl) {
        RocksDB.loadLibrary();
        return WriteBufferManager.newWriteBufferManager(l, l2, bl);
    }

    private static native long newWriteBufferManager(long var0, long var2, boolean var4);

    @Override
    protected void disposeInternal(long l) {
        WriteBufferManager.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

