/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public abstract class ConcurrentTaskLimiter
extends RocksObject {
    protected ConcurrentTaskLimiter(long l) {
        super(l);
    }

    public abstract String name();

    public abstract ConcurrentTaskLimiter setMaxOutstandingTask(int var1);

    public abstract ConcurrentTaskLimiter resetMaxOutstandingTask();

    public abstract int outstandingTask();
}

