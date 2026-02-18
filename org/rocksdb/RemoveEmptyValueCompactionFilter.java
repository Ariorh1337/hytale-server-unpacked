/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.AbstractCompactionFilter;
import org.rocksdb.Slice;

public class RemoveEmptyValueCompactionFilter
extends AbstractCompactionFilter<Slice> {
    public RemoveEmptyValueCompactionFilter() {
        super(RemoveEmptyValueCompactionFilter.createNewRemoveEmptyValueCompactionFilter0());
    }

    private static native long createNewRemoveEmptyValueCompactionFilter0();
}

