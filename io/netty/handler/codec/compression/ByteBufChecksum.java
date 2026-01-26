/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.compression.CompressionUtil;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

abstract class ByteBufChecksum
implements Checksum {
    private final ByteProcessor updateProcessor = new ByteProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            ByteBufChecksum.this.update(value);
            return true;
        }
    };

    ByteBufChecksum() {
    }

    static ByteBufChecksum wrapChecksum(Checksum checksum) {
        ObjectUtil.checkNotNull(checksum, "checksum");
        if (checksum instanceof ByteBufChecksum) {
            return (ByteBufChecksum)checksum;
        }
        return new JdkByteBufChecksum(checksum);
    }

    public void update(ByteBuf b, int off, int len) {
        if (b.hasArray()) {
            this.update(b.array(), b.arrayOffset() + off, len);
        } else {
            b.forEachByte(off, len, this.updateProcessor);
        }
    }

    private static class JdkByteBufChecksum
    extends ByteBufChecksum {
        protected final Checksum checksum;
        private byte[] scratchBuffer;

        JdkByteBufChecksum(Checksum checksum) {
            this.checksum = checksum;
        }

        @Override
        public void update(int b) {
            this.checksum.update(b);
        }

        @Override
        public void update(ByteBuf b, int off, int len) {
            if (b.hasArray()) {
                this.update(b.array(), b.arrayOffset() + off, len);
            } else if (this.checksum instanceof CRC32) {
                ByteBuffer byteBuffer = this.getSafeBuffer(b, off, len);
                ((CRC32)this.checksum).update(byteBuffer);
            } else if (this.checksum instanceof Adler32) {
                ByteBuffer byteBuffer = this.getSafeBuffer(b, off, len);
                ((Adler32)this.checksum).update(byteBuffer);
            } else {
                super.update(b, off, len);
            }
        }

        private ByteBuffer getSafeBuffer(ByteBuf b, int off, int len) {
            ByteBuffer byteBuffer = CompressionUtil.safeNioBuffer(b, off, len);
            int javaVersion = PlatformDependent.javaVersion();
            if (javaVersion >= 22 && javaVersion < 25 && byteBuffer.isDirect()) {
                if (this.scratchBuffer == null || this.scratchBuffer.length < len) {
                    this.scratchBuffer = new byte[len];
                }
                ByteBuffer copy = ByteBuffer.wrap(this.scratchBuffer, 0, len);
                copy.put(byteBuffer).flip();
                return copy;
            }
            return byteBuffer;
        }

        @Override
        public void update(byte[] b, int off, int len) {
            this.checksum.update(b, off, len);
        }

        @Override
        public long getValue() {
            return this.checksum.getValue();
        }

        @Override
        public void reset() {
            this.checksum.reset();
        }
    }
}

