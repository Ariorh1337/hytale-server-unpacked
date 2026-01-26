/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.internal.ObjectUtil;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class ChunkedStream
implements ChunkedInput<ByteBuf> {
    static final int DEFAULT_CHUNK_SIZE = 8192;
    private final PushbackInputStream in;
    private final int chunkSize;
    private long offset;
    private boolean closed;

    public ChunkedStream(InputStream in) {
        this(in, 8192);
    }

    public ChunkedStream(InputStream in, int chunkSize) {
        ObjectUtil.checkNotNull(in, "in");
        ObjectUtil.checkPositive(chunkSize, "chunkSize");
        this.in = in instanceof PushbackInputStream ? (PushbackInputStream)in : new PushbackInputStream(in);
        this.chunkSize = chunkSize;
    }

    public long transferredBytes() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        if (this.closed) {
            return true;
        }
        if (this.in.available() > 0) {
            return false;
        }
        int b = this.in.read();
        if (b < 0) {
            return true;
        }
        this.in.unread(b);
        return false;
    }

    @Override
    public void close() throws Exception {
        this.closed = true;
        this.in.close();
    }

    @Override
    @Deprecated
    public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
        return this.readChunk(ctx.alloc());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
        if (this.isEndOfInput()) {
            return null;
        }
        int availableBytes = this.in.available();
        int chunkSize = availableBytes <= 0 ? this.chunkSize : Math.min(this.chunkSize, this.in.available());
        boolean release = true;
        ByteBuf buffer = allocator.buffer(chunkSize);
        try {
            int written = buffer.writeBytes(this.in, chunkSize);
            if (written < 0) {
                ByteBuf byteBuf = null;
                return byteBuf;
            }
            this.offset += (long)written;
            release = false;
            ByteBuf byteBuf = buffer;
            return byteBuf;
        }
        finally {
            if (release) {
                buffer.release();
            }
        }
    }

    @Override
    public long length() {
        return -1L;
    }

    @Override
    public long progress() {
        return this.offset;
    }
}

