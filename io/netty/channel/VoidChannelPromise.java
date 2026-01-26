/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.concurrent.AbstractFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

public final class VoidChannelPromise
extends AbstractFuture<Void>
implements ChannelPromise {
    private final Channel channel;
    private final ChannelFutureListener fireExceptionListener;

    public VoidChannelPromise(Channel channel, boolean fireException) {
        ObjectUtil.checkNotNull(channel, "channel");
        this.channel = channel;
        this.fireExceptionListener = fireException ? new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Throwable cause = future.cause();
                if (cause != null) {
                    VoidChannelPromise.this.fireException0(cause);
                }
            }
        } : null;
    }

    @Override
    public VoidChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        VoidChannelPromise.fail();
        return this;
    }

    @Override
    public VoidChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>> ... listeners) {
        VoidChannelPromise.fail();
        return this;
    }

    @Override
    public VoidChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        return this;
    }

    @Override
    public VoidChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>> ... listeners) {
        return this;
    }

    @Override
    public VoidChannelPromise await() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) {
        VoidChannelPromise.fail();
        return false;
    }

    @Override
    public boolean await(long timeoutMillis) {
        VoidChannelPromise.fail();
        return false;
    }

    @Override
    public VoidChannelPromise awaitUninterruptibly() {
        VoidChannelPromise.fail();
        return this;
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        VoidChannelPromise.fail();
        return false;
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        VoidChannelPromise.fail();
        return false;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean setUncancellable() {
        return true;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public VoidChannelPromise sync() {
        VoidChannelPromise.fail();
        return this;
    }

    @Override
    public VoidChannelPromise syncUninterruptibly() {
        VoidChannelPromise.fail();
        return this;
    }

    @Override
    public VoidChannelPromise setFailure(Throwable cause) {
        this.fireException0(cause);
        return this;
    }

    @Override
    public VoidChannelPromise setSuccess() {
        return this;
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        this.fireException0(cause);
        return false;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean trySuccess() {
        return false;
    }

    private static void fail() {
        throw new IllegalStateException("void future");
    }

    @Override
    public VoidChannelPromise setSuccess(Void result) {
        return this;
    }

    @Override
    public boolean trySuccess(Void result) {
        return false;
    }

    @Override
    public Void getNow() {
        return null;
    }

    @Override
    public ChannelPromise unvoid() {
        DefaultChannelPromise promise = new DefaultChannelPromise(this.channel);
        if (this.fireExceptionListener != null) {
            promise.addListener((GenericFutureListener)this.fireExceptionListener);
        }
        return promise;
    }

    @Override
    public boolean isVoid() {
        return true;
    }

    private void fireException0(Throwable cause) {
        if (this.fireExceptionListener != null && this.channel.isRegistered()) {
            this.channel.pipeline().fireExceptionCaught(cause);
        }
    }
}

