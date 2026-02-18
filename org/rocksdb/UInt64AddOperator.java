/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.MergeOperator;

public class UInt64AddOperator
extends MergeOperator {
    public UInt64AddOperator() {
        super(UInt64AddOperator.newSharedUInt64AddOperator());
    }

    private static native long newSharedUInt64AddOperator();

    @Override
    protected final void disposeInternal(long l) {
        UInt64AddOperator.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

