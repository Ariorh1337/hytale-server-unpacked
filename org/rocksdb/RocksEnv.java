/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Env;

public class RocksEnv
extends Env {
    RocksEnv(long l) {
        super(l);
    }

    @Override
    protected final void disposeInternal(long l) {
        RocksEnv.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

