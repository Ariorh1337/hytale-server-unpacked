/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;

public class ChunkedNioFile
implements ChunkedInput<ByteBuf> {
    private final FileChannel in;
    private final long startOffset;
    private final long endOffset;
    private final int chunkSize;
    private long offset;

    public ChunkedNioFile(File in) throws IOException {
        this(new RandomAccessFile(in, "r").getChannel());
    }

    public ChunkedNioFile(File in, int chunkSize) throws IOException {
        this(new RandomAccessFile(in, "r").getChannel(), chunkSize);
    }

    public ChunkedNioFile(FileChannel in) throws IOException {
        this(in, 8192);
    }

    public ChunkedNioFile(FileChannel in, int chunkSize) throws IOException {
        this(in, 0L, in.size(), chunkSize);
    }

    public ChunkedNioFile(FileChannel in, long offset, long length, int chunkSize) throws IOException {
        ObjectUtil.checkNotNull(in, "in");
        ObjectUtil.checkPositiveOrZero(offset, "offset");
        ObjectUtil.checkPositiveOrZero(length, "length");
        ObjectUtil.checkPositive(chunkSize, "chunkSize");
        if (!in.isOpen()) {
            throw new ClosedChannelException();
        }
        this.in = in;
        this.chunkSize = chunkSize;
        this.offset = this.startOffset = offset;
        this.endOffset = offset + length;
    }

    public long startOffset() {
        return this.startOffset;
    }

    public long endOffset() {
        return this.endOffset;
    }

    public long currentOffset() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        return this.offset >= this.endOffset || !this.in.isOpen();
    }

    @Override
    public void close() throws Exception {
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
        long offset = this.offset;
        if (offset >= this.endOffset) {
            return null;
        }
        int chunkSize = (int)Math.min((long)this.chunkSize, this.endOffset - offset);
        ByteBuf buffer = allocator.buffer(chunkSize);
        boolean release = true;
        try {
            int localReadBytes;
            int readBytes = 0;
            while ((localReadBytes = buffer.writeBytes(this.in, offset + (long)readBytes, chunkSize - readBytes)) >= 0 && (readBytes += localReadBytes) != chunkSize) {
            }
            this.offset += (long)readBytes;
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
        return this.endOffset - this.startOffset;
    }

    @Override
    public long progress() {
        return this.offset - this.startOffset;
    }
}

