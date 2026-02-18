/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.AbstractNativeReference;

public abstract class RocksMutableObject
extends AbstractNativeReference {
    private long nativeHandle_;
    private boolean owningHandle_;

    protected RocksMutableObject() {
    }

    protected RocksMutableObject(long l) {
        this.nativeHandle_ = l;
        this.owningHandle_ = true;
    }

    public synchronized void resetNativeHandle(long l, boolean bl) {
        this.close();
        this.setNativeHandle(l, bl);
    }

    public synchronized void setNativeHandle(long l, boolean bl) {
        this.nativeHandle_ = l;
        this.owningHandle_ = bl;
    }

    @Override
    protected synchronized boolean isOwningHandle() {
        return this.owningHandle_;
    }

    protected synchronized long getNativeHandle() {
        assert (this.nativeHandle_ != 0L);
        return this.nativeHandle_;
    }

    @Override
    public final synchronized void close() {
        if (this.isOwningHandle()) {
            this.disposeInternal();
            this.owningHandle_ = false;
            this.nativeHandle_ = 0L;
        }
    }

    protected void disposeInternal() {
        this.disposeInternal(this.nativeHandle_);
    }

    protected abstract void disposeInternal(long var1);
}

