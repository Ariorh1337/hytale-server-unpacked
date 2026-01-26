/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteOrder;

public final class Base64 {
    private static final int MAX_LINE_LENGTH = 76;
    private static final byte EQUALS_SIGN = 61;
    private static final byte NEW_LINE = 10;
    private static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;

    private static byte[] alphabet(Base64Dialect dialect) {
        return ObjectUtil.checkNotNull(dialect, (String)"dialect").alphabet;
    }

    private static byte[] decodabet(Base64Dialect dialect) {
        return ObjectUtil.checkNotNull(dialect, (String)"dialect").decodabet;
    }

    private static boolean breakLines(Base64Dialect dialect) {
        return ObjectUtil.checkNotNull(dialect, (String)"dialect").breakLinesByDefault;
    }

    public static ByteBuf encode(ByteBuf src) {
        return Base64.encode(src, Base64Dialect.STANDARD);
    }

    public static ByteBuf encode(ByteBuf src, Base64Dialect dialect) {
        return Base64.encode(src, Base64.breakLines(dialect), dialect);
    }

    public static ByteBuf encode(ByteBuf src, Base64Dialect dialect, boolean addPadding) {
        return Base64.encode(src, Base64.breakLines(dialect), dialect, addPadding);
    }

    public static ByteBuf encode(ByteBuf src, boolean breakLines) {
        return Base64.encode(src, breakLines, Base64Dialect.STANDARD);
    }

    public static ByteBuf encode(ByteBuf src, boolean breakLines, boolean addPadding) {
        return Base64.encode(src, breakLines, Base64Dialect.STANDARD, addPadding);
    }

    public static ByteBuf encode(ByteBuf src, boolean breakLines, Base64Dialect dialect) {
        ObjectUtil.checkNotNull(src, "src");
        ByteBuf dest = Base64.encode(src, src.readerIndex(), src.readableBytes(), breakLines, dialect);
        src.readerIndex(src.writerIndex());
        return dest;
    }

    public static ByteBuf encode(ByteBuf src, boolean breakLines, Base64Dialect dialect, boolean addPadding) {
        ObjectUtil.checkNotNull(src, "src");
        ByteBuf dest = Base64.encode(src, src.readerIndex(), src.readableBytes(), breakLines, dialect, addPadding);
        src.readerIndex(src.writerIndex());
        return dest;
    }

    public static ByteBuf encode(ByteBuf src, int off, int len) {
        return Base64.encode(src, off, len, Base64Dialect.STANDARD);
    }

    public static ByteBuf encode(ByteBuf src, int off, int len, Base64Dialect dialect) {
        return Base64.encode(src, off, len, Base64.breakLines(dialect), dialect);
    }

    public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines) {
        return Base64.encode(src, off, len, breakLines, Base64Dialect.STANDARD);
    }

    public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines, Base64Dialect dialect) {
        return Base64.encode(src, off, len, breakLines, dialect, src.alloc(), true);
    }

    public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines, Base64Dialect dialect, boolean addPadding) {
        return Base64.encode(src, off, len, breakLines, dialect, src.alloc(), addPadding);
    }

    public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines, Base64Dialect dialect, ByteBufAllocator allocator) {
        return Base64.encode(src, off, len, breakLines, dialect, allocator, true);
    }

    private static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines, Base64Dialect dialect, ByteBufAllocator allocator, boolean addPadding) {
        ObjectUtil.checkNotNull(src, "src");
        ObjectUtil.checkNotNull(dialect, "dialect");
        int capacity = Base64.encodedBufferSize(len, breakLines);
        ByteBuf destBuf = allocator.buffer(capacity).order(src.order());
        ByteBuf dest = destBuf.unwrap() == null || !destBuf.isContiguous() ? destBuf : (destBuf.hasArray() ? Unpooled.wrappedBuffer(destBuf.array(), destBuf.arrayOffset(), capacity).order(src.order()) : Unpooled.wrappedBuffer(destBuf.internalNioBuffer(0, capacity)).order(src.order()));
        byte[] alphabet = Base64.alphabet(dialect);
        int d = 0;
        int e = 0;
        int len2 = len - 2;
        int lineLength = 0;
        while (d < len2) {
            Base64.encode3to4(src, d + off, 3, dest, e, alphabet, addPadding);
            if (breakLines && (lineLength += 4) == 76) {
                dest.setByte(e + 4, 10);
                ++e;
                lineLength = 0;
            }
            d += 3;
            e += 4;
        }
        if (d < len) {
            e += Base64.encode3to4(src, d + off, len - d, dest, e, alphabet, addPadding);
        }
        if (e > 1 && dest.getByte(e - 1) == 10) {
            --e;
        }
        if (dest != destBuf) {
            dest.release();
        }
        return destBuf.setIndex(0, e);
    }

    private static int encode3to4(ByteBuf src, int srcOffset, int numSigBytes, ByteBuf dest, int destOffset, byte[] alphabet, boolean addPadding) {
        int inBuff;
        if (src.order() == ByteOrder.BIG_ENDIAN) {
            int inBuff2;
            switch (numSigBytes) {
                case 1: {
                    inBuff2 = Base64.toInt(src.getByte(srcOffset));
                    break;
                }
                case 2: {
                    inBuff2 = Base64.toIntBE(src.getShort(srcOffset));
                    break;
                }
                default: {
                    inBuff2 = numSigBytes <= 0 ? 0 : Base64.toIntBE(src.getMedium(srcOffset));
                }
            }
            return Base64.encode3to4BigEndian(inBuff2, numSigBytes, dest, destOffset, alphabet, addPadding);
        }
        switch (numSigBytes) {
            case 1: {
                inBuff = Base64.toInt(src.getByte(srcOffset));
                break;
            }
            case 2: {
                inBuff = Base64.toIntLE(src.getShort(srcOffset));
                break;
            }
            default: {
                inBuff = numSigBytes <= 0 ? 0 : Base64.toIntLE(src.getMedium(srcOffset));
            }
        }
        return Base64.encode3to4LittleEndian(inBuff, numSigBytes, dest, destOffset, alphabet, addPadding);
    }

    static int encodedBufferSize(int len, boolean breakLines) {
        long len43 = ((long)len << 2) / 3L;
        long ret = len43 + 3L & 0xFFFFFFFFFFFFFFFCL;
        if (breakLines) {
            ret += len43 / 76L;
        }
        return ret < Integer.MAX_VALUE ? (int)ret : Integer.MAX_VALUE;
    }

    private static int toInt(byte value) {
        return (value & 0xFF) << 16;
    }

    private static int toIntBE(short value) {
        return (value & 0xFF00) << 8 | (value & 0xFF) << 8;
    }

    private static int toIntLE(short value) {
        return (value & 0xFF) << 16 | value & 0xFF00;
    }

    private static int toIntBE(int mediumValue) {
        return mediumValue & 0xFF0000 | mediumValue & 0xFF00 | mediumValue & 0xFF;
    }

    private static int toIntLE(int mediumValue) {
        return (mediumValue & 0xFF) << 16 | mediumValue & 0xFF00 | (mediumValue & 0xFF0000) >>> 16;
    }

    private static int encode3to4BigEndian(int inBuff, int numSigBytes, ByteBuf dest, int destOffset, byte[] alphabet, boolean addPadding) {
        switch (numSigBytes) {
            case 3: {
                dest.setInt(destOffset, alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 0x3F] << 16 | alphabet[inBuff >>> 6 & 0x3F] << 8 | alphabet[inBuff & 0x3F]);
                return 4;
            }
            case 2: {
                if (addPadding) {
                    dest.setInt(destOffset, alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 0x3F] << 16 | alphabet[inBuff >>> 6 & 0x3F] << 8 | 0x3D);
                    return 4;
                }
                dest.setMedium(destOffset, alphabet[inBuff >>> 18] << 16 | alphabet[inBuff >>> 12 & 0x3F] << 8 | alphabet[inBuff >>> 6 & 0x3F]);
                return 3;
            }
            case 1: {
                if (addPadding) {
                    dest.setInt(destOffset, alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 0x3F] << 16 | 0x3D00 | 0x3D);
                    return 4;
                }
                dest.setShort(destOffset, alphabet[inBuff >>> 18] << 8 | alphabet[inBuff >>> 12 & 0x3F]);
                return 2;
            }
        }
        return 0;
    }

    private static int encode3to4LittleEndian(int inBuff, int numSigBytes, ByteBuf dest, int destOffset, byte[] alphabet, boolean addPadding) {
        switch (numSigBytes) {
            case 3: {
                dest.setInt(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 0x3F] << 8 | alphabet[inBuff >>> 6 & 0x3F] << 16 | alphabet[inBuff & 0x3F] << 24);
                return 4;
            }
            case 2: {
                if (addPadding) {
                    dest.setInt(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 0x3F] << 8 | alphabet[inBuff >>> 6 & 0x3F] << 16 | 0x3D000000);
                    return 4;
                }
                dest.setMedium(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 0x3F] << 8 | alphabet[inBuff >>> 6 & 0x3F] << 16);
                return 3;
            }
            case 1: {
                if (addPadding) {
                    dest.setInt(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 0x3F] << 8 | 0x3D0000 | 0x3D000000);
                    return 4;
                }
                dest.setShort(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 0x3F] << 8);
                return 2;
            }
        }
        return 0;
    }

    public static ByteBuf decode(ByteBuf src) {
        return Base64.decode(src, Base64Dialect.STANDARD);
    }

    public static ByteBuf decode(ByteBuf src, Base64Dialect dialect) {
        ObjectUtil.checkNotNull(src, "src");
        ByteBuf dest = Base64.decode(src, src.readerIndex(), src.readableBytes(), dialect);
        src.readerIndex(src.writerIndex());
        return dest;
    }

    public static ByteBuf decode(ByteBuf src, int off, int len) {
        return Base64.decode(src, off, len, Base64Dialect.STANDARD);
    }

    public static ByteBuf decode(ByteBuf src, int off, int len, Base64Dialect dialect) {
        return Base64.decode(src, off, len, dialect, src.alloc());
    }

    public static ByteBuf decode(ByteBuf src, int off, int len, Base64Dialect dialect, ByteBufAllocator allocator) {
        ObjectUtil.checkNotNull(src, "src");
        ObjectUtil.checkNotNull(dialect, "dialect");
        return new Decoder().decode(src, off, len, allocator, dialect);
    }

    static int decodedBufferSize(int len) {
        return len - (len >>> 2);
    }

    private Base64() {
    }

    private static final class Decoder
    implements ByteProcessor {
        private final byte[] b4 = new byte[4];
        private int b4Posn;
        private byte[] decodabet;
        private int outBuffPosn;
        private ByteBuf dest;

        private Decoder() {
        }

        ByteBuf decode(ByteBuf src, int off, int len, ByteBufAllocator allocator, Base64Dialect dialect) {
            this.dest = allocator.buffer(Base64.decodedBufferSize(len)).order(src.order());
            this.decodabet = Base64.decodabet(dialect);
            try {
                src.forEachByte(off, len, this);
                if (this.b4Posn == 1) {
                    throw new IllegalArgumentException("Invalid Base64 input, single remaining character implies incorrect length or padding");
                }
                if (this.b4Posn == 2) {
                    this.b4[2] = 61;
                    this.b4[3] = 61;
                    this.outBuffPosn += Decoder.decode4to3(this.b4, this.dest, this.outBuffPosn, this.decodabet);
                } else if (this.b4Posn == 3) {
                    this.b4[3] = 61;
                    this.outBuffPosn += Decoder.decode4to3(this.b4, this.dest, this.outBuffPosn, this.decodabet);
                }
                return this.dest.slice(0, this.outBuffPosn);
            }
            catch (Throwable cause) {
                this.dest.release();
                PlatformDependent.throwException(cause);
                return null;
            }
        }

        @Override
        public boolean process(byte value) throws Exception {
            byte sbiDecode;
            if (value > 0 && (sbiDecode = this.decodabet[value]) >= -5) {
                if (sbiDecode >= -1) {
                    this.b4[this.b4Posn++] = value;
                    if (this.b4Posn > 3) {
                        this.outBuffPosn += Decoder.decode4to3(this.b4, this.dest, this.outBuffPosn, this.decodabet);
                        this.b4Posn = 0;
                        return value != 61;
                    }
                }
                return true;
            }
            throw new IllegalArgumentException("invalid Base64 input character: " + (short)(value & 0xFF) + " (decimal)");
        }

        private static int decode4to3(byte[] src, ByteBuf dest, int destOffset, byte[] decodabet) {
            int decodedValue;
            byte src0 = src[0];
            byte src1 = src[1];
            byte src2 = src[2];
            if (src2 == 61) {
                int decodedValue2;
                try {
                    decodedValue2 = (decodabet[src0] & 0xFF) << 2 | (decodabet[src1] & 0xFF) >>> 4;
                }
                catch (IndexOutOfBoundsException ignored) {
                    throw new IllegalArgumentException("not encoded in Base64");
                }
                dest.setByte(destOffset, decodedValue2);
                return 1;
            }
            byte src3 = src[3];
            if (src3 == 61) {
                int decodedValue3;
                byte b1 = decodabet[src1];
                try {
                    decodedValue3 = dest.order() == ByteOrder.BIG_ENDIAN ? ((decodabet[src0] & 0x3F) << 2 | (b1 & 0xF0) >> 4) << 8 | (b1 & 0xF) << 4 | (decodabet[src2] & 0xFC) >>> 2 : (decodabet[src0] & 0x3F) << 2 | (b1 & 0xF0) >> 4 | ((b1 & 0xF) << 4 | (decodabet[src2] & 0xFC) >>> 2) << 8;
                }
                catch (IndexOutOfBoundsException ignored) {
                    throw new IllegalArgumentException("not encoded in Base64");
                }
                dest.setShort(destOffset, decodedValue3);
                return 2;
            }
            try {
                if (dest.order() == ByteOrder.BIG_ENDIAN) {
                    decodedValue = (decodabet[src0] & 0x3F) << 18 | (decodabet[src1] & 0xFF) << 12 | (decodabet[src2] & 0xFF) << 6 | decodabet[src3] & 0xFF;
                } else {
                    byte b1 = decodabet[src1];
                    byte b2 = decodabet[src2];
                    decodedValue = (decodabet[src0] & 0x3F) << 2 | (b1 & 0xF) << 12 | (b1 & 0xF0) >>> 4 | (b2 & 3) << 22 | (b2 & 0xFC) << 6 | (decodabet[src3] & 0xFF) << 16;
                }
            }
            catch (IndexOutOfBoundsException ignored) {
                throw new IllegalArgumentException("not encoded in Base64");
            }
            dest.setMedium(destOffset, decodedValue);
            return 3;
        }
    }
}

