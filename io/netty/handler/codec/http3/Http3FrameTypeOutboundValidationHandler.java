/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http3.Http3Frame;
import io.netty.handler.codec.http3.Http3FrameValidationUtils;
import io.netty.util.internal.ObjectUtil;

class Http3FrameTypeOutboundValidationHandler<T extends Http3Frame>
extends ChannelOutboundHandlerAdapter {
    private final Class<T> frameType;

    Http3FrameTypeOutboundValidationHandler(Class<T> frameType) {
        this.frameType = ObjectUtil.checkNotNull(frameType, "frameType");
    }

    @Override
    public final void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        Http3Frame frame = (Http3Frame)Http3FrameValidationUtils.validateFrameWritten(this.frameType, msg);
        if (frame != null) {
            this.write(ctx, (T)frame, promise);
        } else {
            this.writeFrameDiscarded(msg, promise);
        }
    }

    void write(ChannelHandlerContext ctx, T msg, ChannelPromise promise) {
        ctx.write(msg, promise);
    }

    void writeFrameDiscarded(Object discardedFrame, ChannelPromise promise) {
        Http3FrameValidationUtils.frameTypeUnexpected(promise, discardedFrame);
    }
}

