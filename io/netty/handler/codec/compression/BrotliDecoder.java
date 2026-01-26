/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aayushatharva.brotli4j.decoder.DecoderJNI$Wrapper
 */
package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.decoder.DecoderJNI;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.Brotli;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;
import java.util.List;

public final class BrotliDecoder
extends ByteToMessageDecoder {
    private final int inputBufferSize;
    private DecoderJNI.Wrapper decoder;
    private boolean destroyed;
    private boolean needsRead;

    public BrotliDecoder() {
        this(8192);
    }

    public BrotliDecoder(int inputBufferSize) {
        this.inputBufferSize = ObjectUtil.checkPositive(inputBufferSize, "inputBufferSize");
    }

    private void forwardOutput(ChannelHandlerContext ctx) {
        ByteBuffer nativeBuffer = this.decoder.pull();
        ByteBuf copy = ctx.alloc().buffer(nativeBuffer.remaining());
        copy.writeBytes(nativeBuffer);
        this.needsRead = false;
        ctx.fireChannelRead(copy);
    }

    private State decompress(ChannelHandlerContext ctx, ByteBuf input) {
        block6: while (true) {
            switch (this.decoder.getStatus()) {
                case DONE: {
                    return State.DONE;
                }
                case OK: {
                    this.decoder.push(0);
                    continue block6;
                }
                case NEEDS_MORE_INPUT: {
                    if (this.decoder.hasOutput()) {
                        this.forwardOutput(ctx);
                    }
                    if (!input.isReadable()) {
                        return State.NEEDS_MORE_INPUT;
                    }
                    ByteBuffer decoderInputBuffer = this.decoder.getInputBuffer();
                    decoderInputBuffer.clear();
                    int readBytes = BrotliDecoder.readBytes(input, decoderInputBuffer);
                    this.decoder.push(readBytes);
                    continue block6;
                }
                case NEEDS_MORE_OUTPUT: {
                    this.forwardOutput(ctx);
                    continue block6;
                }
            }
            break;
        }
        return State.ERROR;
    }

    private static int readBytes(ByteBuf in, ByteBuffer dest) {
        int limit = Math.min(in.readableBytes(), dest.remaining());
        ByteBuffer slice = dest.slice();
        slice.limit(limit);
        in.readBytes(slice);
        dest.position(dest.position() + limit);
        return limit;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.decoder = new DecoderJNI.Wrapper(this.inputBufferSize);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        this.needsRead = true;
        if (this.destroyed) {
            in.skipBytes(in.readableBytes());
            return;
        }
        if (!in.isReadable()) {
            return;
        }
        try {
            State state = this.decompress(ctx, in);
            if (state == State.DONE) {
                this.destroy();
            } else if (state == State.ERROR) {
                throw new DecompressionException("Brotli stream corrupted");
            }
        }
        catch (Exception e) {
            this.destroy();
            throw e;
        }
    }

    private void destroy() {
        if (!this.destroyed) {
            this.destroyed = true;
            this.decoder.destroy();
        }
    }

    @Override
    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        try {
            this.destroy();
        }
        finally {
            super.handlerRemoved0(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            this.destroy();
        }
        finally {
            super.channelInactive(ctx);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.discardSomeReadBytes();
        if (this.needsRead && !ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
        ctx.fireChannelReadComplete();
    }

    static {
        try {
            Brotli.ensureAvailability();
        }
        catch (Throwable throwable) {
            throw new ExceptionInInitializerError(throwable);
        }
    }

    private static enum State {
        DONE,
        NEEDS_MORE_INPUT,
        ERROR;

    }
}

