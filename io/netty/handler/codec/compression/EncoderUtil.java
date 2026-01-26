/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.compression;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class EncoderUtil {
    private static final int THREAD_POOL_DELAY_SECONDS = 10;

    static void closeAfterFinishEncode(final ChannelHandlerContext ctx, ChannelFuture finishFuture, final ChannelPromise promise) {
        if (!finishFuture.isDone()) {
            ScheduledFuture future = ctx.executor().schedule(new Runnable(){

                @Override
                public void run() {
                    ctx.close(promise);
                }
            }, 10L, TimeUnit.SECONDS);
            finishFuture.addListener((GenericFutureListener)new ChannelFutureListener((Future)((Object)future), promise, ctx){
                final /* synthetic */ Future val$future;
                final /* synthetic */ ChannelPromise val$promise;
                final /* synthetic */ ChannelHandlerContext val$ctx;
                {
                    this.val$future = future;
                    this.val$promise = channelPromise;
                    this.val$ctx = channelHandlerContext;
                }

                @Override
                public void operationComplete(ChannelFuture f) {
                    this.val$future.cancel(true);
                    if (!this.val$promise.isDone()) {
                        this.val$ctx.close(this.val$promise);
                    }
                }
            });
        } else {
            ctx.close(promise);
        }
    }

    private EncoderUtil() {
    }
}

