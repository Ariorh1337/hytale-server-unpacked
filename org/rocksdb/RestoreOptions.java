/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public class RestoreOptions
extends RocksObject {
    public RestoreOptions(boolean bl) {
        super(RestoreOptions.newRestoreOptions(bl));
    }

    private static native long newRestoreOptions(boolean var0);

    @Override
    protected final void disposeInternal(long l) {
        RestoreOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

