/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.AbstractCompactionFilter;
import org.rocksdb.Slice;

public class CassandraCompactionFilter
extends AbstractCompactionFilter<Slice> {
    public CassandraCompactionFilter(boolean bl, int n) {
        super(CassandraCompactionFilter.createNewCassandraCompactionFilter0(bl, n));
    }

    private static native long createNewCassandraCompactionFilter0(boolean var0, int var1);
}

