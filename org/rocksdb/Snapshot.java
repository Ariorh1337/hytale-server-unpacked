/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public class Snapshot
extends RocksObject {
    Snapshot(long l) {
        super(l);
        this.disOwnNativeHandle();
    }

    public long getSequenceNumber() {
        return Snapshot.getSequenceNumber(this.nativeHandle_);
    }

    @Override
    protected final void disposeInternal(long l) {
    }

    private static native long getSequenceNumber(long var0);
}

