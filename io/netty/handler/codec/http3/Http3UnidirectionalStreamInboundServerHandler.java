/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http3.Http3CodecUtils;
import io.netty.handler.codec.http3.Http3ControlStreamInboundHandler;
import io.netty.handler.codec.http3.Http3ControlStreamOutboundHandler;
import io.netty.handler.codec.http3.Http3ErrorCode;
import io.netty.handler.codec.http3.Http3FrameCodec;
import io.netty.handler.codec.http3.Http3UnidirectionalStreamInboundHandler;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

final class Http3UnidirectionalStreamInboundServerHandler
extends Http3UnidirectionalStreamInboundHandler {
    Http3UnidirectionalStreamInboundServerHandler(Http3FrameCodec.Http3FrameCodecFactory codecFactory, Http3ControlStreamInboundHandler localControlStreamHandler, Http3ControlStreamOutboundHandler remoteControlStreamHandler, @Nullable LongFunction<ChannelHandler> unknownStreamHandlerFactory, Supplier<ChannelHandler> qpackEncoderHandlerFactory, Supplier<ChannelHandler> qpackDecoderHandlerFactory) {
        super(codecFactory, localControlStreamHandler, remoteControlStreamHandler, unknownStreamHandlerFactory, qpackEncoderHandlerFactory, qpackDecoderHandlerFactory);
    }

    @Override
    void initPushStream(ChannelHandlerContext ctx, long id) {
        Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_STREAM_CREATION_ERROR, "Server received push stream.", false);
    }
}

