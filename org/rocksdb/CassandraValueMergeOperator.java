/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.MergeOperator;

public class CassandraValueMergeOperator
extends MergeOperator {
    public CassandraValueMergeOperator(int n) {
        super(CassandraValueMergeOperator.newSharedCassandraValueMergeOperator(n, 0));
    }

    public CassandraValueMergeOperator(int n, int n2) {
        super(CassandraValueMergeOperator.newSharedCassandraValueMergeOperator(n, n2));
    }

    private static native long newSharedCassandraValueMergeOperator(int var0, int var1);

    @Override
    protected final void disposeInternal(long l) {
        CassandraValueMergeOperator.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

