/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class WebSocket00FrameDecoder
extends ReplayingDecoder<Void>
implements WebSocketFrameDecoder {
    static final int DEFAULT_MAX_FRAME_SIZE = 16384;
    private final long maxFrameSize;
    private boolean receivedClosingHandshake;

    public WebSocket00FrameDecoder() {
        this(16384);
    }

    public WebSocket00FrameDecoder(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    public WebSocket00FrameDecoder(WebSocketDecoderConfig decoderConfig) {
        this.maxFrameSize = ObjectUtil.checkNotNull(decoderConfig, "decoderConfig").maxFramePayloadLength();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (this.receivedClosingHandshake) {
            in.skipBytes(this.actualReadableBytes());
            return;
        }
        byte type = in.readByte();
        WebSocketFrame frame = (type & 0x80) == 128 ? this.decodeBinaryFrame(ctx, type, in) : this.decodeTextFrame(ctx, in);
        if (frame != null) {
            out.add(frame);
        }
    }

    private WebSocketFrame decodeBinaryFrame(ChannelHandlerContext ctx, byte type, ByteBuf buffer) {
        byte b;
        long frameSize = 0L;
        int lengthFieldSize = 0;
        do {
            b = buffer.readByte();
            frameSize <<= 7;
            if ((frameSize |= (long)(b & 0x7F)) > this.maxFrameSize) {
                throw new TooLongFrameException("frame length exceeds " + this.maxFrameSize + ": " + frameSize);
            }
            if (++lengthFieldSize <= 8) continue;
            throw new TooLongFrameException("frame length field size exceeds 8: " + lengthFieldSize);
        } while ((b & 0x80) == 128);
        if (type == -1 && frameSize == 0L) {
            this.receivedClosingHandshake = true;
            return new CloseWebSocketFrame(true, 0, ctx.alloc().buffer(0));
        }
        ByteBuf payload = ByteBufUtil.readBytes(ctx.alloc(), buffer, (int)frameSize);
        return new BinaryWebSocketFrame(payload);
    }

    private WebSocketFrame decodeTextFrame(ChannelHandlerContext ctx, ByteBuf buffer) {
        int rbytes;
        int ridx = buffer.readerIndex();
        int delimPos = buffer.indexOf(ridx, ridx + (rbytes = this.actualReadableBytes()), (byte)-1);
        if (delimPos == -1) {
            if ((long)rbytes > this.maxFrameSize) {
                throw new TooLongFrameException("frame length exceeds " + this.maxFrameSize + ": " + rbytes);
            }
            return null;
        }
        int frameSize = delimPos - ridx;
        if ((long)frameSize > this.maxFrameSize) {
            throw new TooLongFrameException("frame length exceeds " + this.maxFrameSize + ": " + frameSize);
        }
        ByteBuf binaryData = ByteBufUtil.readBytes(ctx.alloc(), buffer, frameSize);
        buffer.skipBytes(1);
        int ffDelimPos = binaryData.indexOf(binaryData.readerIndex(), binaryData.writerIndex(), (byte)-1);
        if (ffDelimPos >= 0) {
            binaryData.release();
            throw new IllegalArgumentException("a text frame should not contain 0xFF.");
        }
        return new TextWebSocketFrame(binaryData);
    }
}

