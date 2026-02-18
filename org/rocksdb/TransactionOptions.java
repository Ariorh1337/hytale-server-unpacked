/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;
import org.rocksdb.TransactionalOptions;

public class TransactionOptions
extends RocksObject
implements TransactionalOptions<TransactionOptions> {
    public TransactionOptions() {
        super(TransactionOptions.newTransactionOptions());
    }

    @Override
    public boolean isSetSnapshot() {
        assert (this.isOwningHandle());
        return TransactionOptions.isSetSnapshot(this.nativeHandle_);
    }

    @Override
    public TransactionOptions setSetSnapshot(boolean bl) {
        assert (this.isOwningHandle());
        TransactionOptions.setSetSnapshot(this.nativeHandle_, bl);
        return this;
    }

    public boolean isDeadlockDetect() {
        assert (this.isOwningHandle());
        return TransactionOptions.isDeadlockDetect(this.nativeHandle_);
    }

    public TransactionOptions setDeadlockDetect(boolean bl) {
        assert (this.isOwningHandle());
        TransactionOptions.setDeadlockDetect(this.nativeHandle_, bl);
        return this;
    }

    public long getLockTimeout() {
        assert (this.isOwningHandle());
        return TransactionOptions.getLockTimeout(this.nativeHandle_);
    }

    public TransactionOptions setLockTimeout(long l) {
        assert (this.isOwningHandle());
        TransactionOptions.setLockTimeout(this.nativeHandle_, l);
        return this;
    }

    public long getExpiration() {
        assert (this.isOwningHandle());
        return TransactionOptions.getExpiration(this.nativeHandle_);
    }

    public TransactionOptions setExpiration(long l) {
        assert (this.isOwningHandle());
        TransactionOptions.setExpiration(this.nativeHandle_, l);
        return this;
    }

    public long getDeadlockDetectDepth() {
        return TransactionOptions.getDeadlockDetectDepth(this.nativeHandle_);
    }

    public TransactionOptions setDeadlockDetectDepth(long l) {
        TransactionOptions.setDeadlockDetectDepth(this.nativeHandle_, l);
        return this;
    }

    public long getMaxWriteBatchSize() {
        return TransactionOptions.getMaxWriteBatchSize(this.nativeHandle_);
    }

    public TransactionOptions setMaxWriteBatchSize(long l) {
        TransactionOptions.setMaxWriteBatchSize(this.nativeHandle_, l);
        return this;
    }

    private static native long newTransactionOptions();

    private static native boolean isSetSnapshot(long var0);

    private static native void setSetSnapshot(long var0, boolean var2);

    private static native boolean isDeadlockDetect(long var0);

    private static native void setDeadlockDetect(long var0, boolean var2);

    private static native long getLockTimeout(long var0);

    private static native void setLockTimeout(long var0, long var2);

    private static native long getExpiration(long var0);

    private static native void setExpiration(long var0, long var2);

    private static native long getDeadlockDetectDepth(long var0);

    private static native void setDeadlockDetectDepth(long var0, long var2);

    private static native long getMaxWriteBatchSize(long var0);

    private static native void setMaxWriteBatchSize(long var0, long var2);

    @Override
    protected final void disposeInternal(long l) {
        TransactionOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

