/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http3.Http3ConnectionHandler;
import io.netty.handler.codec.http3.Http3ErrorCode;
import io.netty.handler.codec.http3.Http3Exception;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.handler.codec.quic.QuicStreamType;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import org.jetbrains.annotations.Nullable;

final class Http3CodecUtils {
    static final long MIN_RESERVED_FRAME_TYPE = 64L;
    static final long MAX_RESERVED_FRAME_TYPE = 66571993090L;
    static final int HTTP3_DATA_FRAME_TYPE = 0;
    static final int HTTP3_HEADERS_FRAME_TYPE = 1;
    static final int HTTP3_CANCEL_PUSH_FRAME_TYPE = 3;
    static final int HTTP3_SETTINGS_FRAME_TYPE = 4;
    static final int HTTP3_PUSH_PROMISE_FRAME_TYPE = 5;
    static final int HTTP3_GO_AWAY_FRAME_TYPE = 7;
    static final int HTTP3_MAX_PUSH_ID_FRAME_TYPE = 13;
    static final int HTTP3_CANCEL_PUSH_FRAME_MAX_LEN = 8;
    static final int HTTP3_SETTINGS_FRAME_MAX_LEN = 256;
    static final int HTTP3_GO_AWAY_FRAME_MAX_LEN = 8;
    static final int HTTP3_MAX_PUSH_ID_FRAME_MAX_LEN = 8;
    static final int HTTP3_CONTROL_STREAM_TYPE = 0;
    static final int HTTP3_PUSH_STREAM_TYPE = 1;
    static final int HTTP3_QPACK_ENCODER_STREAM_TYPE = 2;
    static final int HTTP3_QPACK_DECODER_STREAM_TYPE = 3;

    private Http3CodecUtils() {
    }

    static long checkIsReservedFrameType(long type) {
        return ObjectUtil.checkInRange(type, 64L, 66571993090L, "type");
    }

    static boolean isReservedFrameType(long type) {
        return type >= 64L && type <= 66571993090L;
    }

    static boolean isServerInitiatedQuicStream(QuicStreamChannel channel) {
        return channel.streamId() % 2L != 0L;
    }

    static boolean isReservedHttp2FrameType(long type) {
        switch ((int)type) {
            case 2: 
            case 6: 
            case 8: 
            case 9: {
                return true;
            }
        }
        return false;
    }

    static boolean isReservedHttp2Setting(long key) {
        return 2L <= key && key <= 5L;
    }

    static int numBytesForVariableLengthInteger(long value) {
        if (value <= 63L) {
            return 1;
        }
        if (value <= 16383L) {
            return 2;
        }
        if (value <= 0x3FFFFFFFL) {
            return 4;
        }
        if (value <= 0x3FFFFFFFFFFFFFFFL) {
            return 8;
        }
        throw new IllegalArgumentException();
    }

    static void writeVariableLengthInteger(ByteBuf out, long value) {
        int numBytes = Http3CodecUtils.numBytesForVariableLengthInteger(value);
        Http3CodecUtils.writeVariableLengthInteger(out, value, numBytes);
    }

    static void writeVariableLengthInteger(ByteBuf out, long value, int numBytes) {
        int writerIndex = out.writerIndex();
        switch (numBytes) {
            case 1: {
                out.writeByte((byte)value);
                break;
            }
            case 2: {
                out.writeShort((short)value);
                Http3CodecUtils.encodeLengthIntoBuffer(out, writerIndex, (byte)64);
                break;
            }
            case 4: {
                out.writeInt((int)value);
                Http3CodecUtils.encodeLengthIntoBuffer(out, writerIndex, (byte)-128);
                break;
            }
            case 8: {
                out.writeLong(value);
                Http3CodecUtils.encodeLengthIntoBuffer(out, writerIndex, (byte)-64);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    private static void encodeLengthIntoBuffer(ByteBuf out, int index, byte b) {
        out.setByte(index, out.getByte(index) | b);
    }

    static long readVariableLengthInteger(ByteBuf in, int len) {
        switch (len) {
            case 1: {
                return in.readUnsignedByte();
            }
            case 2: {
                return in.readUnsignedShort() & 0x3FFF;
            }
            case 4: {
                return in.readUnsignedInt() & 0x3FFFFFFFL;
            }
            case 8: {
                return in.readLong() & 0x3FFFFFFFFFFFFFFFL;
            }
        }
        throw new IllegalArgumentException();
    }

    static int numBytesForVariableLengthInteger(byte b) {
        byte val = (byte)(b >> 6);
        if ((val & 1) != 0) {
            if ((val & 2) != 0) {
                return 8;
            }
            return 2;
        }
        if ((val & 2) != 0) {
            return 4;
        }
        return 1;
    }

    static void criticalStreamClosed(ChannelHandlerContext ctx) {
        if (ctx.channel().parent().isActive()) {
            Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_CLOSED_CRITICAL_STREAM, "Critical stream closed.", false);
        }
    }

    static void connectionError(ChannelHandlerContext ctx, Http3Exception exception, boolean fireException) {
        if (fireException) {
            ctx.fireExceptionCaught(exception);
        }
        Http3CodecUtils.connectionError(ctx.channel(), exception.errorCode(), exception.getMessage());
    }

    static void connectionError(ChannelHandlerContext ctx, Http3ErrorCode errorCode, @Nullable String msg, boolean fireException) {
        if (fireException) {
            ctx.fireExceptionCaught(new Http3Exception(errorCode, msg));
        }
        Http3CodecUtils.connectionError(ctx.channel(), errorCode, msg);
    }

    static void closeOnFailure(ChannelFuture future) {
        if (future.isDone() && !future.isSuccess()) {
            future.channel().close();
            return;
        }
        future.addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    static void connectionError(Channel channel, Http3ErrorCode errorCode, @Nullable String msg) {
        ByteBuf buffer;
        QuicChannel quicChannel = channel instanceof QuicChannel ? (QuicChannel)channel : (QuicChannel)channel.parent();
        if (msg != null) {
            buffer = quicChannel.alloc().buffer();
            buffer.writeCharSequence(msg, CharsetUtil.US_ASCII);
        } else {
            buffer = Unpooled.EMPTY_BUFFER;
        }
        quicChannel.close(true, errorCode.code, buffer);
    }

    static void streamError(ChannelHandlerContext ctx, Http3ErrorCode errorCode) {
        ((QuicStreamChannel)ctx.channel()).shutdownOutput(errorCode.code);
    }

    static void readIfNoAutoRead(ChannelHandlerContext ctx) {
        if (!ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
    }

    @Nullable
    static Http3ConnectionHandler getConnectionHandlerOrClose(QuicChannel ch) {
        Http3ConnectionHandler connectionHandler = ch.pipeline().get(Http3ConnectionHandler.class);
        if (connectionHandler == null) {
            Http3CodecUtils.connectionError(ch, Http3ErrorCode.H3_INTERNAL_ERROR, "Couldn't obtain the " + StringUtil.simpleClassName(Http3ConnectionHandler.class) + " of the parent Channel");
            return null;
        }
        return connectionHandler;
    }

    static void verifyIsUnidirectional(QuicStreamChannel ch) {
        if (ch.type() != QuicStreamType.UNIDIRECTIONAL) {
            throw new IllegalArgumentException("Invalid stream type: " + (Object)((Object)ch.type()) + " for stream: " + ch.streamId());
        }
    }
}

