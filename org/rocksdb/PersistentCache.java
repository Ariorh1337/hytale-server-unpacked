/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Env;
import org.rocksdb.Logger;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksObject;

public class PersistentCache
extends RocksObject {
    public PersistentCache(Env env, String string, long l, Logger logger, boolean bl) throws RocksDBException {
        super(PersistentCache.newPersistentCache(env.nativeHandle_, string, l, logger.nativeHandle_, bl));
    }

    private static native long newPersistentCache(long var0, String var2, long var3, long var5, boolean var7) throws RocksDBException;

    @Override
    protected final void disposeInternal(long l) {
        PersistentCache.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

