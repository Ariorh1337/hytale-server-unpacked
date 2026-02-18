/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.MemTableConfig;

public class SkipListMemTableConfig
extends MemTableConfig {
    public static final long DEFAULT_LOOKAHEAD = 0L;
    private long lookahead_ = 0L;

    public SkipListMemTableConfig setLookahead(long l) {
        this.lookahead_ = l;
        return this;
    }

    public long lookahead() {
        return this.lookahead_;
    }

    @Override
    protected long newMemTableFactoryHandle() {
        return SkipListMemTableConfig.newMemTableFactoryHandle0(this.lookahead_);
    }

    private static native long newMemTableFactoryHandle0(long var0) throws IllegalArgumentException;
}

