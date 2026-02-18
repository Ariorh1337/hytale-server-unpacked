/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksCallbackObject;
import org.rocksdb.Snapshot;

public abstract class AbstractTransactionNotifier
extends RocksCallbackObject {
    protected AbstractTransactionNotifier() {
        super(new long[0]);
    }

    public abstract void snapshotCreated(Snapshot var1);

    private void snapshotCreated(long l) {
        this.snapshotCreated(new Snapshot(l));
    }

    @Override
    protected long initializeNative(long ... lArray) {
        return this.createNewTransactionNotifier();
    }

    private native long createNewTransactionNotifier();

    @Override
    protected void disposeInternal() {
        this.disposeInternal(this.nativeHandle_);
    }

    protected final void disposeInternal(long l) {
        AbstractTransactionNotifier.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

