/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http3.Http3FrameValidationUtils;
import io.netty.handler.codec.http3.Http3HeadersFrame;
import io.netty.handler.codec.http3.Http3PushPromiseFrame;
import io.netty.handler.codec.http3.Http3RequestStreamCodecState;
import io.netty.handler.codec.http3.Http3RequestStreamFrame;
import io.netty.handler.codec.http3.Http3UnknownFrame;
import org.jetbrains.annotations.Nullable;

final class Http3RequestStreamEncodeStateValidator
extends ChannelOutboundHandlerAdapter
implements Http3RequestStreamCodecState {
    private State state = State.None;

    Http3RequestStreamEncodeStateValidator() {
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof Http3RequestStreamFrame)) {
            super.write(ctx, msg, promise);
            return;
        }
        Http3RequestStreamFrame frame = (Http3RequestStreamFrame)msg;
        State nextState = Http3RequestStreamEncodeStateValidator.evaluateFrame(this.state, frame);
        if (nextState == null) {
            Http3FrameValidationUtils.frameTypeUnexpected(ctx, msg);
            return;
        }
        this.state = nextState;
        super.write(ctx, msg, promise);
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

    @Nullable
    static State evaluateFrame(State state, Http3RequestStreamFrame frame) {
        if (frame instanceof Http3PushPromiseFrame || frame instanceof Http3UnknownFrame) {
            return state;
        }
        switch (state) {
            case None: 
            case Headers: {
                if (!(frame instanceof Http3HeadersFrame)) {
                    return null;
                }
                return Http3RequestStreamEncodeStateValidator.isInformationalResponse((Http3HeadersFrame)frame) ? State.Headers : State.FinalHeaders;
            }
            case FinalHeaders: {
                if (frame instanceof Http3HeadersFrame) {
                    if (Http3RequestStreamEncodeStateValidator.isInformationalResponse((Http3HeadersFrame)frame)) {
                        return null;
                    }
                    return State.Trailers;
                }
                return state;
            }
            case Trailers: {
                return null;
            }
        }
        throw new Error("Unexpected frame state: " + (Object)((Object)state));
    }

    static boolean isStreamStarted(State state) {
        return state != State.None;
    }

    static boolean isFinalHeadersReceived(State state) {
        return Http3RequestStreamEncodeStateValidator.isStreamStarted(state) && state != State.Headers;
    }

    static boolean isTrailersReceived(State state) {
        return state == State.Trailers;
    }

    private static boolean isInformationalResponse(Http3HeadersFrame headersFrame) {
        return HttpStatusClass.valueOf(headersFrame.headers().status()) == HttpStatusClass.INFORMATIONAL;
    }

    static enum State {
        None,
        Headers,
        FinalHeaders,
        Trailers;

    }
}

