/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.AbstractHttpData;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public abstract class AbstractMemoryHttpData
extends AbstractHttpData {
    private ByteBuf byteBuf = Unpooled.EMPTY_BUFFER;
    private int chunkPosition;

    protected AbstractMemoryHttpData(String name, Charset charset, long size) {
        super(name, charset, size);
    }

    @Override
    public void setContent(ByteBuf buffer) throws IOException {
        ObjectUtil.checkNotNull(buffer, "buffer");
        long localsize = buffer.readableBytes();
        try {
            this.checkSize(localsize);
        }
        catch (IOException e) {
            buffer.release();
            throw e;
        }
        if (this.definedSize > 0L && this.definedSize < localsize) {
            buffer.release();
            throw new IOException("Out of size: " + localsize + " > " + this.definedSize);
        }
        if (this.byteBuf != null) {
            this.byteBuf.release();
        }
        this.byteBuf = buffer;
        this.size = localsize;
        this.setCompleted();
    }

    @Override
    public void setContent(InputStream inputStream) throws IOException {
        ObjectUtil.checkNotNull(inputStream, "inputStream");
        byte[] bytes = new byte[16384];
        ByteBuf buffer = Unpooled.buffer();
        int written = 0;
        try {
            int read = inputStream.read(bytes);
            while (read > 0) {
                buffer.writeBytes(bytes, 0, read);
                this.checkSize(written += read);
                read = inputStream.read(bytes);
            }
        }
        catch (IOException e) {
            buffer.release();
            throw e;
        }
        this.size = written;
        if (this.definedSize > 0L && this.definedSize < this.size) {
            buffer.release();
            throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
        }
        if (this.byteBuf != null) {
            this.byteBuf.release();
        }
        this.byteBuf = buffer;
        this.setCompleted();
    }

    @Override
    public void addContent(ByteBuf buffer, boolean last) throws IOException {
        if (buffer != null) {
            long localsize = buffer.readableBytes();
            try {
                this.checkSize(this.size + localsize);
            }
            catch (IOException e) {
                buffer.release();
                throw e;
            }
            if (this.definedSize > 0L && this.definedSize < this.size + localsize) {
                buffer.release();
                throw new IOException("Out of size: " + (this.size + localsize) + " > " + this.definedSize);
            }
            this.size += localsize;
            if (this.byteBuf == null) {
                this.byteBuf = buffer;
            } else if (localsize == 0L) {
                buffer.release();
            } else if (this.byteBuf.readableBytes() == 0) {
                this.byteBuf.release();
                this.byteBuf = buffer;
            } else if (this.byteBuf instanceof CompositeByteBuf) {
                CompositeByteBuf cbb = (CompositeByteBuf)this.byteBuf;
                cbb.addComponent(true, buffer);
            } else {
                CompositeByteBuf cbb = Unpooled.compositeBuffer(Integer.MAX_VALUE);
                cbb.addComponents(true, this.byteBuf, buffer);
                this.byteBuf = cbb;
            }
        }
        if (last) {
            this.setCompleted();
        } else {
            ObjectUtil.checkNotNull(buffer, "buffer");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setContent(File file) throws IOException {
        ByteBuffer byteBuffer;
        ObjectUtil.checkNotNull(file, "file");
        long newsize = file.length();
        if (newsize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("File too big to be loaded in memory");
        }
        this.checkSize(newsize);
        try (RandomAccessFile accessFile = new RandomAccessFile(file, "r");
             FileChannel fileChannel = accessFile.getChannel();){
            byte[] array = new byte[(int)newsize];
            byteBuffer = ByteBuffer.wrap(array);
            int read = 0;
            while ((long)read < newsize) {
                read += fileChannel.read(byteBuffer);
            }
        }
        byteBuffer.flip();
        if (this.byteBuf != null) {
            this.byteBuf.release();
        }
        this.byteBuf = Unpooled.wrappedBuffer(Integer.MAX_VALUE, byteBuffer);
        this.size = newsize;
        this.setCompleted();
    }

    @Override
    public void delete() {
        if (this.byteBuf != null) {
            this.byteBuf.release();
            this.byteBuf = null;
        }
    }

    @Override
    public byte[] get() {
        if (this.byteBuf == null) {
            return Unpooled.EMPTY_BUFFER.array();
        }
        byte[] array = new byte[this.byteBuf.readableBytes()];
        this.byteBuf.getBytes(this.byteBuf.readerIndex(), array);
        return array;
    }

    @Override
    public String getString() {
        return this.getString(HttpConstants.DEFAULT_CHARSET);
    }

    @Override
    public String getString(Charset encoding) {
        if (this.byteBuf == null) {
            return "";
        }
        if (encoding == null) {
            encoding = HttpConstants.DEFAULT_CHARSET;
        }
        return this.byteBuf.toString(encoding);
    }

    @Override
    public ByteBuf getByteBuf() {
        return this.byteBuf;
    }

    @Override
    public ByteBuf getChunk(int length) throws IOException {
        if (this.byteBuf == null || length == 0 || this.byteBuf.readableBytes() == 0) {
            this.chunkPosition = 0;
            return Unpooled.EMPTY_BUFFER;
        }
        int sizeLeft = this.byteBuf.readableBytes() - this.chunkPosition;
        if (sizeLeft == 0) {
            this.chunkPosition = 0;
            return Unpooled.EMPTY_BUFFER;
        }
        int sliceLength = length;
        if (sizeLeft < length) {
            sliceLength = sizeLeft;
        }
        ByteBuf chunk = this.byteBuf.retainedSlice(this.chunkPosition, sliceLength);
        this.chunkPosition += sliceLength;
        return chunk;
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean renameTo(File dest) throws IOException {
        long written;
        ObjectUtil.checkNotNull(dest, "dest");
        if (this.byteBuf == null) {
            if (!dest.createNewFile()) {
                throw new IOException("file exists already: " + dest);
            }
            return true;
        }
        int length = this.byteBuf.readableBytes();
        try (RandomAccessFile accessFile = new RandomAccessFile(dest, "rw");
             FileChannel fileChannel = accessFile.getChannel();){
            if (this.byteBuf.nioBufferCount() == 1) {
                ByteBuffer byteBuffer = this.byteBuf.nioBuffer();
                for (written = 0L; written < (long)length; written += (long)fileChannel.write(byteBuffer)) {
                }
            } else {
                ByteBuffer[] byteBuffers = this.byteBuf.nioBuffers();
                while (written < (long)length) {
                    written += fileChannel.write(byteBuffers);
                }
            }
            fileChannel.force(false);
        }
        return written == (long)length;
    }

    @Override
    public File getFile() throws IOException {
        throw new IOException("Not represented by a file");
    }

    @Override
    public HttpData touch() {
        return this.touch(null);
    }

    @Override
    public HttpData touch(Object hint) {
        if (this.byteBuf != null) {
            this.byteBuf.touch(hint);
        }
        return this;
    }
}

