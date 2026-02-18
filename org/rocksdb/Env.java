/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.rocksdb.Priority;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksEnv;
import org.rocksdb.RocksObject;
import org.rocksdb.ThreadStatus;

public abstract class Env
extends RocksObject {
    private static final AtomicReference<RocksEnv> SINGULAR_DEFAULT_ENV = new AtomicReference<Object>(null);

    public static Env getDefault() {
        RocksEnv rocksEnv;
        RocksEnv rocksEnv2 = null;
        while ((rocksEnv = SINGULAR_DEFAULT_ENV.get()) == null) {
            if (rocksEnv2 == null) {
                RocksDB.loadLibrary();
                rocksEnv2 = new RocksEnv(Env.getDefaultEnvInternal());
                rocksEnv2.disOwnNativeHandle();
            }
            SINGULAR_DEFAULT_ENV.compareAndSet(null, rocksEnv2);
        }
        return rocksEnv;
    }

    public Env setBackgroundThreads(int n) {
        return this.setBackgroundThreads(n, Priority.LOW);
    }

    public int getBackgroundThreads(Priority priority) {
        return Env.getBackgroundThreads(this.nativeHandle_, priority.getValue());
    }

    public Env setBackgroundThreads(int n, Priority priority) {
        Env.setBackgroundThreads(this.nativeHandle_, n, priority.getValue());
        return this;
    }

    public int getThreadPoolQueueLen(Priority priority) {
        return Env.getThreadPoolQueueLen(this.nativeHandle_, priority.getValue());
    }

    public Env incBackgroundThreadsIfNeeded(int n, Priority priority) {
        Env.incBackgroundThreadsIfNeeded(this.nativeHandle_, n, priority.getValue());
        return this;
    }

    public Env lowerThreadPoolIOPriority(Priority priority) {
        Env.lowerThreadPoolIOPriority(this.nativeHandle_, priority.getValue());
        return this;
    }

    public Env lowerThreadPoolCPUPriority(Priority priority) {
        Env.lowerThreadPoolCPUPriority(this.nativeHandle_, priority.getValue());
        return this;
    }

    public List<ThreadStatus> getThreadList() throws RocksDBException {
        return Arrays.asList(Env.getThreadList(this.nativeHandle_));
    }

    Env(long l) {
        super(l);
    }

    private static native long getDefaultEnvInternal();

    private static native void setBackgroundThreads(long var0, int var2, byte var3);

    private static native int getBackgroundThreads(long var0, byte var2);

    private static native int getThreadPoolQueueLen(long var0, byte var2);

    private static native void incBackgroundThreadsIfNeeded(long var0, int var2, byte var3);

    private static native void lowerThreadPoolIOPriority(long var0, byte var2);

    private static native void lowerThreadPoolCPUPriority(long var0, byte var2);

    private static native ThreadStatus[] getThreadList(long var0) throws RocksDBException;
}

