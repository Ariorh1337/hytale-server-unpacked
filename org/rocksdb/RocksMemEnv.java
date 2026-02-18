/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Env;

public class RocksMemEnv
extends Env {
    public RocksMemEnv(Env env) {
        super(RocksMemEnv.createMemEnv(env.nativeHandle_));
    }

    private static native long createMemEnv(long var0);

    @Override
    protected final void disposeInternal(long l) {
        RocksMemEnv.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

