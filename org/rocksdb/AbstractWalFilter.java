/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksCallbackObject;
import org.rocksdb.WalFilter;
import org.rocksdb.WriteBatch;

public abstract class AbstractWalFilter
extends RocksCallbackObject
implements WalFilter {
    public AbstractWalFilter() {
        super(new long[0]);
    }

    @Override
    protected long initializeNative(long ... lArray) {
        return this.createNewWalFilter();
    }

    private short logRecordFoundProxy(long l, String string, long l2, long l3) {
        WalFilter.LogRecordFoundResult logRecordFoundResult = this.logRecordFound(l, string, new WriteBatch(l2), new WriteBatch(l3));
        return AbstractWalFilter.logRecordFoundResultToShort(logRecordFoundResult);
    }

    private static short logRecordFoundResultToShort(WalFilter.LogRecordFoundResult logRecordFoundResult) {
        short s = (short)(logRecordFoundResult.walProcessingOption.getValue() << 8);
        return (short)(s | (logRecordFoundResult.batchChanged ? (short)1 : 0));
    }

    private native long createNewWalFilter();
}

