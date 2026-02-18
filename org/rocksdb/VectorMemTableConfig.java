/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.MemTableConfig;

public class VectorMemTableConfig
extends MemTableConfig {
    public static final int DEFAULT_RESERVED_SIZE = 0;
    private int reservedSize_ = 0;

    public VectorMemTableConfig setReservedSize(int n) {
        this.reservedSize_ = n;
        return this;
    }

    public int reservedSize() {
        return this.reservedSize_;
    }

    @Override
    protected long newMemTableFactoryHandle() {
        return VectorMemTableConfig.newMemTableFactoryHandle(this.reservedSize_);
    }

    private static native long newMemTableFactoryHandle(long var0) throws IllegalArgumentException;
}

