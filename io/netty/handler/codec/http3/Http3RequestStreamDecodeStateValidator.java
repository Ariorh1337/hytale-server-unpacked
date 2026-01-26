/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http3.Http3FrameValidationUtils;
import io.netty.handler.codec.http3.Http3RequestStreamCodecState;
import io.netty.handler.codec.http3.Http3RequestStreamEncodeStateValidator;
import io.netty.handler.codec.http3.Http3RequestStreamFrame;

final class Http3RequestStreamDecodeStateValidator
extends ChannelInboundHandlerAdapter
implements Http3RequestStreamCodecState {
    private Http3RequestStreamEncodeStateValidator.State state = Http3RequestStreamEncodeStateValidator.State.None;

    Http3RequestStreamDecodeStateValidator() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Http3RequestStreamFrame)) {
            super.channelRead(ctx, msg);
            return;
        }
        Http3RequestStreamFrame frame = (Http3RequestStreamFrame)msg;
        Http3RequestStreamEncodeStateValidator.State nextState = Http3RequestStreamEncodeStateValidator.evaluateFrame(this.state, frame);
        if (nextState == null) {
            Http3FrameValidationUtils.frameTypeUnexpected(ctx, msg);
            return;
        }
        this.state = nextState;
        super.channelRead(ctx, msg);
    }

    @Override
    public boolean started() {
        return Http3RequestStreamEncodeStateValidator.isStreamStarted(this.state);
    }

    @Override
    public boolean receivedFinalHeaders() {
        return Http3RequestStreamEncodeStateValidator.isFinalHeadersReceived(this.state);
    }

    @Override
    public boolean terminated() {
        return Http3RequestStreamEncodeStateValidator.isTrailersReceived(this.state);
    }
}

