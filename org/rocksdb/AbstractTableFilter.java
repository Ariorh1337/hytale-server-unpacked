/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksCallbackObject;
import org.rocksdb.TableFilter;

public abstract class AbstractTableFilter
extends RocksCallbackObject
implements TableFilter {
    protected AbstractTableFilter() {
        super(new long[0]);
    }

    @Override
    protected long initializeNative(long ... lArray) {
        return this.createNewTableFilter();
    }

    private native long createNewTableFilter();
}

