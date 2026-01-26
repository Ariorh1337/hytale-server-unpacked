/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http3.Http3CodecUtils;
import io.netty.handler.codec.http3.Http3ErrorCode;
import io.netty.handler.codec.http3.Http3Exception;
import io.netty.handler.codec.http3.QpackEncoder;
import io.netty.handler.codec.http3.QpackException;
import io.netty.handler.codec.http3.QpackUtil;
import java.util.List;

final class QpackDecoderHandler
extends ByteToMessageDecoder {
    private boolean discard;
    private final QpackEncoder qpackEncoder;

    QpackDecoderHandler(QpackEncoder qpackEncoder) {
        this.qpackEncoder = qpackEncoder;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) {
            return;
        }
        if (this.discard) {
            in.skipBytes(in.readableBytes());
            return;
        }
        byte b = in.getByte(in.readerIndex());
        if ((b & 0x80) == 128) {
            long streamId = QpackUtil.decodePrefixedInteger(in, 7);
            if (streamId < 0L) {
                return;
            }
            try {
                this.qpackEncoder.sectionAcknowledgment(streamId);
            }
            catch (QpackException e) {
                Http3CodecUtils.connectionError(ctx, new Http3Exception(Http3ErrorCode.QPACK_DECODER_STREAM_ERROR, "Section acknowledgment decode failed.", e), true);
            }
            return;
        }
        if ((b & 0xC0) == 64) {
            long streamId = QpackUtil.decodePrefixedInteger(in, 6);
            if (streamId < 0L) {
                return;
            }
            try {
                this.qpackEncoder.streamCancellation(streamId);
            }
            catch (QpackException e) {
                Http3CodecUtils.connectionError(ctx, new Http3Exception(Http3ErrorCode.QPACK_DECODER_STREAM_ERROR, "Stream cancellation decode failed.", e), true);
            }
            return;
        }
        if ((b & 0xC0) == 0) {
            int increment = QpackUtil.decodePrefixedIntegerAsInt(in, 6);
            if (increment == 0) {
                this.discard = true;
                Http3CodecUtils.connectionError(ctx, Http3ErrorCode.QPACK_DECODER_STREAM_ERROR, "Invalid increment '" + increment + "'.", false);
                return;
            }
            if (increment < 0) {
                return;
            }
            try {
                this.qpackEncoder.insertCountIncrement(increment);
            }
            catch (QpackException e) {
                Http3CodecUtils.connectionError(ctx, new Http3Exception(Http3ErrorCode.QPACK_DECODER_STREAM_ERROR, "Insert count increment decode failed.", e), true);
            }
            return;
        }
        this.discard = true;
        Http3CodecUtils.connectionError(ctx, Http3ErrorCode.QPACK_DECODER_STREAM_ERROR, "Unknown decoder instruction '" + b + "'.", false);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.fireChannelReadComplete();
        Http3CodecUtils.readIfNoAutoRead(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof ChannelInputShutdownEvent) {
            Http3CodecUtils.criticalStreamClosed(ctx);
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Http3CodecUtils.criticalStreamClosed(ctx);
        ctx.fireChannelInactive();
    }
}

