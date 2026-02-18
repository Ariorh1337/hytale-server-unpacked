/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.ConcurrentTaskLimiter;

public class ConcurrentTaskLimiterImpl
extends ConcurrentTaskLimiter {
    public ConcurrentTaskLimiterImpl(String string, int n) {
        super(ConcurrentTaskLimiterImpl.newConcurrentTaskLimiterImpl0(string, n));
    }

    @Override
    public String name() {
        assert (this.isOwningHandle());
        return ConcurrentTaskLimiterImpl.name(this.nativeHandle_);
    }

    @Override
    public ConcurrentTaskLimiter setMaxOutstandingTask(int n) {
        assert (this.isOwningHandle());
        ConcurrentTaskLimiterImpl.setMaxOutstandingTask(this.nativeHandle_, n);
        return this;
    }

    @Override
    public ConcurrentTaskLimiter resetMaxOutstandingTask() {
        assert (this.isOwningHandle());
        ConcurrentTaskLimiterImpl.resetMaxOutstandingTask(this.nativeHandle_);
        return this;
    }

    @Override
    public int outstandingTask() {
        assert (this.isOwningHandle());
        return ConcurrentTaskLimiterImpl.outstandingTask(this.nativeHandle_);
    }

    private static native long newConcurrentTaskLimiterImpl0(String var0, int var1);

    private static native String name(long var0);

    private static native void setMaxOutstandingTask(long var0, int var2);

    private static native void resetMaxOutstandingTask(long var0);

    private static native int outstandingTask(long var0);

    @Override
    protected final void disposeInternal(long l) {
        ConcurrentTaskLimiterImpl.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

