/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class AutoCloseBase
implements Closeable {
    private static final AtomicIntegerFieldUpdater<AutoCloseBase> SHARED_LOCK_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AutoCloseBase.class, "sharedLock");
    private static final int SHARED_LOCK_CLOSED = -1;
    private volatile int sharedLock;

    AutoCloseBase() {
    }

    void storeFence() {
        this.sharedLock = 0;
    }

    void acquireSharedLock() {
        int n;
        do {
            if ((n = this.sharedLock) < 0) {
                throw new IllegalStateException("Closed");
            }
            if (n != Integer.MAX_VALUE) continue;
            throw new IllegalStateException("Shared lock overflow");
        } while (!SHARED_LOCK_UPDATER.compareAndSet(this, n, n + 1));
    }

    void releaseSharedLock() {
        int n;
        do {
            if ((n = this.sharedLock) < 0) {
                throw new IllegalStateException("Closed");
            }
            if (n != 0) continue;
            throw new IllegalStateException("Shared lock underflow");
        } while (!SHARED_LOCK_UPDATER.compareAndSet(this, n, n - 1));
    }

    abstract void doClose();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        AutoCloseBase autoCloseBase = this;
        synchronized (autoCloseBase) {
            if (this.sharedLock == -1) {
                return;
            }
            if (!SHARED_LOCK_UPDATER.compareAndSet(this, 0, -1)) {
                throw new IllegalStateException("Attempt to close while in use");
            }
            this.doClose();
        }
    }
}

