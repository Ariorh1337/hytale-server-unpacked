/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.SucceededFuture;
import io.netty.util.concurrent.ThreadAwareExecutor;

public interface EventExecutor
extends EventExecutorGroup,
ThreadAwareExecutor {
    public EventExecutorGroup parent();

    @Override
    default public boolean isExecutorThread(Thread thread) {
        return this.inEventLoop(thread);
    }

    default public boolean inEventLoop() {
        return this.inEventLoop(Thread.currentThread());
    }

    public boolean inEventLoop(Thread var1);

    default public <V> Promise<V> newPromise() {
        return new DefaultPromise(this);
    }

    default public <V> ProgressivePromise<V> newProgressivePromise() {
        return new DefaultProgressivePromise(this);
    }

    default public <V> Future<V> newSucceededFuture(V result) {
        return new SucceededFuture<V>(this, result);
    }

    default public <V> Future<V> newFailedFuture(Throwable cause) {
        return new FailedFuture(this, cause);
    }

    default public boolean isSuspended() {
        return false;
    }

    default public boolean trySuspend() {
        return false;
    }
}

