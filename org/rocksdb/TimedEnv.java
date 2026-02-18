/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Env;

public class TimedEnv
extends Env {
    public TimedEnv(Env env) {
        super(TimedEnv.createTimedEnv(env.nativeHandle_));
    }

    private static native long createTimedEnv(long var0);

    @Override
    protected final void disposeInternal(long l) {
        TimedEnv.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

