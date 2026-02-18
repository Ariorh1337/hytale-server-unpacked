/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;
import org.rocksdb.TxnDBWritePolicy;

public class TransactionDBOptions
extends RocksObject {
    public TransactionDBOptions() {
        super(TransactionDBOptions.newTransactionDBOptions());
    }

    public long getMaxNumLocks() {
        assert (this.isOwningHandle());
        return TransactionDBOptions.getMaxNumLocks(this.nativeHandle_);
    }

    public TransactionDBOptions setMaxNumLocks(long l) {
        assert (this.isOwningHandle());
        TransactionDBOptions.setMaxNumLocks(this.nativeHandle_, l);
        return this;
    }

    public long getNumStripes() {
        assert (this.isOwningHandle());
        return TransactionDBOptions.getNumStripes(this.nativeHandle_);
    }

    public TransactionDBOptions setNumStripes(long l) {
        assert (this.isOwningHandle());
        TransactionDBOptions.setNumStripes(this.nativeHandle_, l);
        return this;
    }

    public long getTransactionLockTimeout() {
        assert (this.isOwningHandle());
        return TransactionDBOptions.getTransactionLockTimeout(this.nativeHandle_);
    }

    public TransactionDBOptions setTransactionLockTimeout(long l) {
        assert (this.isOwningHandle());
        TransactionDBOptions.setTransactionLockTimeout(this.nativeHandle_, l);
        return this;
    }

    public long getDefaultLockTimeout() {
        assert (this.isOwningHandle());
        return TransactionDBOptions.getDefaultLockTimeout(this.nativeHandle_);
    }

    public TransactionDBOptions setDefaultLockTimeout(long l) {
        assert (this.isOwningHandle());
        TransactionDBOptions.setDefaultLockTimeout(this.nativeHandle_, l);
        return this;
    }

    public TxnDBWritePolicy getWritePolicy() {
        assert (this.isOwningHandle());
        return TxnDBWritePolicy.getTxnDBWritePolicy(TransactionDBOptions.getWritePolicy(this.nativeHandle_));
    }

    public TransactionDBOptions setWritePolicy(TxnDBWritePolicy txnDBWritePolicy) {
        assert (this.isOwningHandle());
        TransactionDBOptions.setWritePolicy(this.nativeHandle_, txnDBWritePolicy.getValue());
        return this;
    }

    private static native long newTransactionDBOptions();

    private static native long getMaxNumLocks(long var0);

    private static native void setMaxNumLocks(long var0, long var2);

    private static native long getNumStripes(long var0);

    private static native void setNumStripes(long var0, long var2);

    private static native long getTransactionLockTimeout(long var0);

    private static native void setTransactionLockTimeout(long var0, long var2);

    private static native long getDefaultLockTimeout(long var0);

    private static native void setDefaultLockTimeout(long var0, long var2);

    private static native byte getWritePolicy(long var0);

    private static native void setWritePolicy(long var0, byte var2);

    @Override
    protected final void disposeInternal(long l) {
        TransactionDBOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

