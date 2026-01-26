/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.compression.ByteBufChecksum;
import io.netty.handler.codec.compression.DecompressionException;
import java.nio.ByteBuffer;

final class CompressionUtil {
    private CompressionUtil() {
    }

    static void checkChecksum(ByteBufChecksum checksum, ByteBuf uncompressed, int currentChecksum) {
        checksum.reset();
        checksum.update(uncompressed, uncompressed.readerIndex(), uncompressed.readableBytes());
        int checksumResult = (int)checksum.getValue();
        if (checksumResult != currentChecksum) {
            throw new DecompressionException(String.format("stream corrupted: mismatching checksum: %d (expected: %d)", checksumResult, currentChecksum));
        }
    }

    static ByteBuffer safeReadableNioBuffer(ByteBuf buffer) {
        return CompressionUtil.safeNioBuffer(buffer, buffer.readerIndex(), buffer.readableBytes());
    }

    static ByteBuffer safeNioBuffer(ByteBuf buffer, int index, int length) {
        return buffer.nioBufferCount() == 1 ? buffer.internalNioBuffer(index, length) : buffer.nioBuffer(index, length);
    }
}

