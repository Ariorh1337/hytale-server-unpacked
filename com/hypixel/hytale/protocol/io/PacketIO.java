/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.io;

import com.github.luben.zstd.Zstd;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.PacketRegistry;
import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PacketIO {
    public static final int FRAME_HEADER_SIZE = 4;
    public static final Charset UTF8 = StandardCharsets.UTF_8;
    public static final Charset ASCII = StandardCharsets.US_ASCII;
    private static final int COMPRESSION_LEVEL = Integer.getInteger("hytale.protocol.compressionLevel", Zstd.defaultCompressionLevel());

    private PacketIO() {
    }

    public static float readHalfLE(@Nonnull ByteBuf buf, int index) {
        short bits = buf.getShortLE(index);
        return PacketIO.halfToFloat(bits);
    }

    public static void writeHalfLE(@Nonnull ByteBuf buf, float value) {
        buf.writeShortLE(PacketIO.floatToHalf(value));
    }

    @Nonnull
    public static byte[] readBytes(@Nonnull ByteBuf buf, int offset, int length) {
        byte[] bytes = new byte[length];
        buf.getBytes(offset, bytes);
        return bytes;
    }

    @Nonnull
    public static byte[] readByteArray(@Nonnull ByteBuf buf, int offset, int length) {
        byte[] result = new byte[length];
        buf.getBytes(offset, result);
        return result;
    }

    @Nonnull
    public static short[] readShortArrayLE(@Nonnull ByteBuf buf, int offset, int length) {
        short[] result = new short[length];
        for (int i = 0; i < length; ++i) {
            result[i] = buf.getShortLE(offset + i * 2);
        }
        return result;
    }

    @Nonnull
    public static float[] readFloatArrayLE(@Nonnull ByteBuf buf, int offset, int length) {
        float[] result = new float[length];
        for (int i = 0; i < length; ++i) {
            result[i] = buf.getFloatLE(offset + i * 4);
        }
        return result;
    }

    @Nonnull
    public static String readFixedAsciiString(@Nonnull ByteBuf buf, int offset, int length) {
        int end;
        byte[] bytes = new byte[length];
        buf.getBytes(offset, bytes);
        for (end = 0; end < length && bytes[end] != 0; ++end) {
        }
        return new String(bytes, 0, end, StandardCharsets.US_ASCII);
    }

    @Nonnull
    public static String readFixedString(@Nonnull ByteBuf buf, int offset, int length) {
        int end;
        byte[] bytes = new byte[length];
        buf.getBytes(offset, bytes);
        for (end = 0; end < length && bytes[end] != 0; ++end) {
        }
        return new String(bytes, 0, end, StandardCharsets.UTF_8);
    }

    @Nonnull
    public static String readVarString(@Nonnull ByteBuf buf, int offset) {
        return PacketIO.readVarString(buf, offset, StandardCharsets.UTF_8);
    }

    @Nonnull
    public static String readVarAsciiString(@Nonnull ByteBuf buf, int offset) {
        return PacketIO.readVarString(buf, offset, StandardCharsets.US_ASCII);
    }

    @Nonnull
    public static String readVarString(@Nonnull ByteBuf buf, int offset, Charset charset) {
        int len = VarInt.peek(buf, offset);
        int varIntLen = VarInt.length(buf, offset);
        byte[] bytes = new byte[len];
        buf.getBytes(offset + varIntLen, bytes);
        return new String(bytes, charset);
    }

    public static int utf8ByteLength(@Nonnull String s) {
        int len = 0;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c < '\u0080') {
                ++len;
                continue;
            }
            if (c < '\u0800') {
                len += 2;
                continue;
            }
            if (Character.isHighSurrogate(c)) {
                len += 4;
                ++i;
                continue;
            }
            len += 3;
        }
        return len;
    }

    public static int stringSize(@Nonnull String s) {
        int len = PacketIO.utf8ByteLength(s);
        return VarInt.size(len) + len;
    }

    public static void writeFixedBytes(@Nonnull ByteBuf buf, @Nonnull byte[] data, int length) {
        buf.writeBytes(data, 0, Math.min(data.length, length));
        for (int i = data.length; i < length; ++i) {
            buf.writeByte(0);
        }
    }

    public static void writeFixedAsciiString(@Nonnull ByteBuf buf, @Nullable String value, int length) {
        if (value != null) {
            byte[] bytes = value.getBytes(StandardCharsets.US_ASCII);
            if (bytes.length > length) {
                throw new ProtocolException("Fixed ASCII string exceeds length: " + bytes.length + " > " + length);
            }
            buf.writeBytes(bytes);
            buf.writeZero(length - bytes.length);
        } else {
            buf.writeZero(length);
        }
    }

    public static void writeFixedString(@Nonnull ByteBuf buf, @Nullable String value, int length) {
        if (value != null) {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            if (bytes.length > length) {
                throw new ProtocolException("Fixed UTF-8 string exceeds length: " + bytes.length + " > " + length);
            }
            buf.writeBytes(bytes);
            buf.writeZero(length - bytes.length);
        } else {
            buf.writeZero(length);
        }
    }

    public static void writeVarString(@Nonnull ByteBuf buf, @Nonnull String value, int maxLength) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > maxLength) {
            throw new ProtocolException("String exceeds max bytes: " + bytes.length + " > " + maxLength);
        }
        VarInt.write(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    public static void writeVarAsciiString(@Nonnull ByteBuf buf, @Nonnull String value, int maxLength) {
        byte[] bytes = value.getBytes(StandardCharsets.US_ASCII);
        if (bytes.length > maxLength) {
            throw new ProtocolException("String exceeds max bytes: " + bytes.length + " > " + maxLength);
        }
        VarInt.write(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    @Nonnull
    public static UUID readUUID(@Nonnull ByteBuf buf, int offset) {
        long mostSig = buf.getLong(offset);
        long leastSig = buf.getLong(offset + 8);
        return new UUID(mostSig, leastSig);
    }

    public static void writeUUID(@Nonnull ByteBuf buf, @Nonnull UUID value) {
        buf.writeLong(value.getMostSignificantBits());
        buf.writeLong(value.getLeastSignificantBits());
    }

    private static float halfToFloat(short half) {
        int h = half & 0xFFFF;
        int sign = h >>> 15 & 1;
        int exp = h >>> 10 & 0x1F;
        int mant = h & 0x3FF;
        if (exp == 0) {
            if (mant == 0) {
                return sign == 0 ? 0.0f : -0.0f;
            }
            exp = 1;
            while ((mant & 0x400) == 0) {
                mant <<= 1;
                --exp;
            }
            mant &= 0x3FF;
        } else if (exp == 31) {
            return mant == 0 ? (sign == 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY) : Float.NaN;
        }
        int floatBits = sign << 31 | exp + 112 << 23 | mant << 13;
        return Float.intBitsToFloat(floatBits);
    }

    private static short floatToHalf(float f) {
        int bits = Float.floatToRawIntBits(f);
        int sign = bits >>> 16 & 0x8000;
        int val = (bits & Integer.MAX_VALUE) + 4096;
        if (val >= 1199570944) {
            if ((bits & Integer.MAX_VALUE) >= 1199570944) {
                if (val < 2139095040) {
                    return (short)(sign | 0x7C00);
                }
                return (short)(sign | 0x7C00 | (bits & 0x7FFFFF) >>> 13);
            }
            return (short)(sign | 0x7BFF);
        }
        if (val >= 0x38800000) {
            return (short)(sign | val - 0x38000000 >>> 13);
        }
        if (val < 0x33000000) {
            return (short)sign;
        }
        val = (bits & Integer.MAX_VALUE) >>> 23;
        return (short)(sign | (bits & 0x7FFFFF | 0x800000) + (0x800000 >>> val - 102) >>> 126 - val);
    }

    private static int compressToBuffer(@Nonnull ByteBuf src, @Nonnull ByteBuf dst, int dstOffset, int maxDstSize) {
        if (src.isDirect() && dst.isDirect()) {
            return Zstd.compress(dst.nioBuffer(dstOffset, maxDstSize), src.nioBuffer(), COMPRESSION_LEVEL);
        }
        int srcSize = src.readableBytes();
        byte[] srcBytes = new byte[srcSize];
        src.getBytes(src.readerIndex(), srcBytes);
        byte[] compressed = Zstd.compress(srcBytes, COMPRESSION_LEVEL);
        dst.setBytes(dstOffset, compressed);
        return compressed.length;
    }

    @Nonnull
    private static ByteBuf decompressFromBuffer(@Nonnull ByteBuf src, int srcOffset, int srcLength, int maxDecompressedSize) {
        if (srcLength > maxDecompressedSize) {
            throw new ProtocolException("Compressed size " + srcLength + " exceeds max decompressed size " + maxDecompressedSize);
        }
        if (src.isDirect()) {
            ByteBuffer srcNio = src.nioBuffer(srcOffset, srcLength);
            long decompressedSize = Zstd.getFrameContentSize(srcNio);
            if (decompressedSize < 0L) {
                throw new ProtocolException("Invalid Zstd frame or unknown content size");
            }
            if (decompressedSize > (long)maxDecompressedSize) {
                throw new ProtocolException("Decompressed size " + decompressedSize + " exceeds maximum " + maxDecompressedSize);
            }
            ByteBuf dst = Unpooled.directBuffer((int)decompressedSize);
            ByteBuffer dstNio = dst.nioBuffer(0, (int)decompressedSize);
            int result = Zstd.decompress(dstNio, srcNio);
            if (Zstd.isError(result)) {
                dst.release();
                throw new ProtocolException("Zstd decompression failed: " + Zstd.getErrorName(result));
            }
            dst.writerIndex(result);
            return dst;
        }
        byte[] srcBytes = new byte[srcLength];
        src.getBytes(srcOffset, srcBytes);
        long decompressedSize = Zstd.getFrameContentSize(srcBytes);
        if (decompressedSize < 0L) {
            throw new ProtocolException("Invalid Zstd frame or unknown content size");
        }
        if (decompressedSize > (long)maxDecompressedSize) {
            throw new ProtocolException("Decompressed size " + decompressedSize + " exceeds maximum " + maxDecompressedSize);
        }
        byte[] decompressed = Zstd.decompress(srcBytes, (int)decompressedSize);
        return Unpooled.wrappedBuffer(decompressed);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void writeFramedPacket(@Nonnull Packet packet, @Nonnull Class<? extends Packet> packetClass, @Nonnull ByteBuf out, @Nonnull PacketStatsRecorder statsRecorder) {
        Integer id = PacketRegistry.getId(packetClass);
        if (id == null) {
            throw new ProtocolException("Unknown packet type: " + packetClass.getName());
        }
        PacketRegistry.PacketInfo info = PacketRegistry.getById(id);
        int lengthIndex = out.writerIndex();
        out.writeIntLE(0);
        out.writeIntLE(id);
        ByteBuf payloadBuf = Unpooled.buffer(Math.min(info.maxSize(), 65536));
        try {
            packet.serialize(payloadBuf);
            int serializedSize = payloadBuf.readableBytes();
            if (serializedSize > info.maxSize()) {
                throw new ProtocolException("Packet " + info.name() + " serialized to " + serializedSize + " bytes, exceeds max size " + info.maxSize());
            }
            if (info.compressed() && serializedSize > 0) {
                int compressBound = (int)Zstd.compressBound(serializedSize);
                out.ensureWritable(compressBound);
                int compressedSize = PacketIO.compressToBuffer(payloadBuf, out, out.writerIndex(), compressBound);
                if (Zstd.isError(compressedSize)) {
                    throw new ProtocolException("Zstd compression failed: " + Zstd.getErrorName(compressedSize));
                }
                if (compressedSize > 0x64000000) {
                    throw new ProtocolException("Packet " + info.name() + " compressed payload size " + compressedSize + " exceeds protocol maximum");
                }
                out.writerIndex(out.writerIndex() + compressedSize);
                out.setIntLE(lengthIndex, compressedSize);
                statsRecorder.recordSend(id, serializedSize, compressedSize);
            } else {
                if (serializedSize > 0x64000000) {
                    throw new ProtocolException("Packet " + info.name() + " payload size " + serializedSize + " exceeds protocol maximum");
                }
                out.writeBytes(payloadBuf);
                out.setIntLE(lengthIndex, serializedSize);
                statsRecorder.recordSend(id, serializedSize, 0);
            }
        }
        finally {
            payloadBuf.release();
        }
    }

    @Nonnull
    public static Packet readFramedPacket(@Nonnull ByteBuf in, int payloadLength, @Nonnull PacketStatsRecorder statsRecorder) {
        int packetId = in.readIntLE();
        PacketRegistry.PacketInfo info = PacketRegistry.getById(packetId);
        if (info == null) {
            in.skipBytes(payloadLength);
            throw new ProtocolException("Unknown packet ID: " + packetId);
        }
        return PacketIO.readFramedPacketWithInfo(in, payloadLength, info, statsRecorder);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public static Packet readFramedPacketWithInfo(@Nonnull ByteBuf in, int payloadLength, @Nonnull PacketRegistry.PacketInfo info, @Nonnull PacketStatsRecorder statsRecorder) {
        int uncompressedSize;
        ByteBuf payload;
        int compressedSize = 0;
        if (info.compressed() && payloadLength > 0) {
            try {
                payload = PacketIO.decompressFromBuffer(in, in.readerIndex(), payloadLength, info.maxSize());
            }
            catch (ProtocolException e) {
                in.skipBytes(payloadLength);
                throw e;
            }
            in.skipBytes(payloadLength);
            uncompressedSize = payload.readableBytes();
            compressedSize = payloadLength;
        } else if (payloadLength > 0) {
            payload = in.readRetainedSlice(payloadLength);
            uncompressedSize = payloadLength;
        } else {
            payload = Unpooled.EMPTY_BUFFER;
            uncompressedSize = 0;
        }
        try {
            Packet packet = info.deserialize().apply(payload, 0);
            statsRecorder.recordReceive(info.id(), uncompressedSize, compressedSize);
            Packet packet2 = packet;
            return packet2;
        }
        finally {
            if (payloadLength > 0) {
                payload.release();
            }
        }
    }
}

