/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.transport;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import org.jetbrains.annotations.NotNull;

public final class ReusableCountLatch {
    @NotNull
    private final Sync sync;

    public ReusableCountLatch(int initialCount) {
        if (initialCount < 0) {
            throw new IllegalArgumentException("negative initial count '" + initialCount + "' is not allowed");
        }
        this.sync = new Sync(initialCount);
    }

    public ReusableCountLatch() {
        this(0);
    }

    public int getCount() {
        return this.sync.getCount();
    }

    public void decrement() {
        this.sync.decrement();
    }

    public void increment() {
        this.sync.increment();
    }

    public void waitTillZero() throws InterruptedException {
        this.sync.acquireSharedInterruptibly(1);
    }

    public boolean waitTillZero(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return this.sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    private static final class Sync
    extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 5970133580157457018L;

        Sync(int count) {
            this.setState(count);
        }

        private int getCount() {
            return this.getState();
        }

        private void increment() {
            int newCount;
            int oldCount;
            while (!this.compareAndSetState(oldCount = this.getState(), newCount = oldCount + 1)) {
            }
        }

        private void decrement() {
            this.releaseShared(1);
        }

        @Override
        public int tryAcquireShared(int acquires) {
            return this.getState() == 0 ? 1 : -1;
        }

        @Override
        public boolean tryReleaseShared(int releases) {
            int newCount;
            int oldCount;
            do {
                if ((oldCount = this.getState()) != 0) continue;
                return false;
            } while (!this.compareAndSetState(oldCount, newCount = oldCount - 1));
            return newCount == 0;
        }
    }
}

