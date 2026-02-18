/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.MergeOperator;

public class StringAppendOperator
extends MergeOperator {
    public StringAppendOperator() {
        this(',');
    }

    public StringAppendOperator(char c) {
        super(StringAppendOperator.newSharedStringAppendOperator(c));
    }

    public StringAppendOperator(String string) {
        super(StringAppendOperator.newSharedStringAppendOperator(string));
    }

    private static native long newSharedStringAppendOperator(char var0);

    private static native long newSharedStringAppendOperator(String var0);

    @Override
    protected final void disposeInternal(long l) {
        StringAppendOperator.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

