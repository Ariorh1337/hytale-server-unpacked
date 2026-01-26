/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http3.Http3Frame;
import io.netty.handler.codec.http3.Http3FrameValidationUtils;
import io.netty.util.internal.ObjectUtil;

class Http3FrameTypeInboundValidationHandler<T extends Http3Frame>
extends ChannelInboundHandlerAdapter {
    protected final Class<T> frameType;

    Http3FrameTypeInboundValidationHandler(Class<T> frameType) {
        this.frameType = ObjectUtil.checkNotNull(frameType, "frameType");
    }

    @Override
    public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Http3Frame frame = (Http3Frame)Http3FrameValidationUtils.validateFrameRead(this.frameType, msg);
        if (frame != null) {
            this.channelRead(ctx, (T)frame);
        } else {
            this.readFrameDiscarded(ctx, msg);
        }
    }

    void channelRead(ChannelHandlerContext ctx, T frame) throws Exception {
        ctx.fireChannelRead(frame);
    }

    void readFrameDiscarded(ChannelHandlerContext ctx, Object discardedFrame) {
        Http3FrameValidationUtils.frameTypeUnexpected(ctx, discardedFrame);
    }
}

