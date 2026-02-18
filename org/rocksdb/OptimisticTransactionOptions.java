/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.AbstractComparator;
import org.rocksdb.RocksObject;
import org.rocksdb.TransactionalOptions;

public class OptimisticTransactionOptions
extends RocksObject
implements TransactionalOptions<OptimisticTransactionOptions> {
    public OptimisticTransactionOptions() {
        super(OptimisticTransactionOptions.newOptimisticTransactionOptions());
    }

    @Override
    public boolean isSetSnapshot() {
        assert (this.isOwningHandle());
        return OptimisticTransactionOptions.isSetSnapshot(this.nativeHandle_);
    }

    @Override
    public OptimisticTransactionOptions setSetSnapshot(boolean bl) {
        assert (this.isOwningHandle());
        OptimisticTransactionOptions.setSetSnapshot(this.nativeHandle_, bl);
        return this;
    }

    public OptimisticTransactionOptions setComparator(AbstractComparator abstractComparator) {
        assert (this.isOwningHandle());
        OptimisticTransactionOptions.setComparator(this.nativeHandle_, abstractComparator.nativeHandle_);
        return this;
    }

    private static native long newOptimisticTransactionOptions();

    private static native boolean isSetSnapshot(long var0);

    private static native void setSetSnapshot(long var0, boolean var2);

    private static native void setComparator(long var0, long var2);

    @Override
    protected final void disposeInternal(long l) {
        OptimisticTransactionOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

