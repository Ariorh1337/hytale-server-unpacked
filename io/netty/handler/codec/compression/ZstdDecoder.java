/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.compression;

import com.github.luben.zstd.ZstdInputStreamNoFinalizer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.CompressionUtil;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.Zstd;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class ZstdDecoder
extends ByteToMessageDecoder {
    private final int maximumAllocationSize;
    private final MutableByteBufInputStream inputStream;
    private ZstdInputStreamNoFinalizer zstdIs;
    private boolean needsRead;
    private State currentState;

    public ZstdDecoder() {
        this(0x400000);
    }

    public ZstdDecoder(int maximumAllocationSize) {
        try {
            Zstd.ensureAvailability();
        }
        catch (Throwable throwable) {
            throw new ExceptionInInitializerError(throwable);
        }
        this.inputStream = new MutableByteBufInputStream();
        this.currentState = State.DECOMPRESS_DATA;
        this.maximumAllocationSize = ObjectUtil.checkPositiveOrZero(maximumAllocationSize, "maximumAllocationSize");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        this.needsRead = true;
        try {
            if (this.currentState == State.CORRUPTED) {
                in.skipBytes(in.readableBytes());
                return;
            }
            this.inputStream.current = in;
            ReferenceCounted outBuffer = null;
            int compressedLength = in.readableBytes();
            try {
                int w;
                long uncompressedLength = in.isDirect() ? com.github.luben.zstd.Zstd.getFrameContentSize(CompressionUtil.safeNioBuffer(in, in.readerIndex(), in.readableBytes())) : com.github.luben.zstd.Zstd.getFrameContentSize(in.array(), in.readerIndex() + in.arrayOffset(), in.readableBytes());
                if (uncompressedLength <= 0L) {
                    uncompressedLength = (long)compressedLength * 2L;
                }
                do {
                    if (outBuffer == null) {
                        outBuffer = ctx.alloc().heapBuffer((int)(this.maximumAllocationSize == 0 ? uncompressedLength : Math.min((long)this.maximumAllocationSize, uncompressedLength)));
                    }
                    while ((w = ((ByteBuf)outBuffer).writeBytes(this.zstdIs, ((ByteBuf)outBuffer).writableBytes())) != -1 && ((ByteBuf)outBuffer).isWritable()) {
                    }
                    if (!((ByteBuf)outBuffer).isReadable()) continue;
                    this.needsRead = false;
                    ctx.fireChannelRead(outBuffer);
                    outBuffer = null;
                } while (w != -1);
            }
            finally {
                if (outBuffer != null) {
                    outBuffer.release();
                }
            }
        }
        catch (Exception e) {
            this.currentState = State.CORRUPTED;
            throw new DecompressionException(e);
        }
        finally {
            this.inputStream.current = null;
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

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        this.zstdIs = new ZstdInputStreamNoFinalizer(this.inputStream);
        this.zstdIs.setContinuous(true);
    }

    @Override
    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        try {
            ZstdDecoder.closeSilently(this.zstdIs);
        }
        finally {
            super.handlerRemoved0(ctx);
        }
    }

    private static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private static final class MutableByteBufInputStream
    extends InputStream {
        ByteBuf current;

        private MutableByteBufInputStream() {
        }

        @Override
        public int read() {
            if (this.current == null || !this.current.isReadable()) {
                return -1;
            }
            return this.current.readByte() & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) {
            int available = this.available();
            if (available == 0) {
                return -1;
            }
            len = Math.min(available, len);
            this.current.readBytes(b, off, len);
            return len;
        }

        @Override
        public int available() {
            return this.current == null ? 0 : this.current.readableBytes();
        }
    }

    private static enum State {
        DECOMPRESS_DATA,
        CORRUPTED;

    }
}

