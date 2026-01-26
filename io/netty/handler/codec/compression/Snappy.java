/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.compression.Crc32c;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.Arrays;

public final class Snappy {
    private static final int MAX_HT_SIZE = 16384;
    private static final int MIN_COMPRESSIBLE_BYTES = 15;
    private static final int PREAMBLE_NOT_FULL = -1;
    private static final int NOT_ENOUGH_INPUT = -1;
    private static final int LITERAL = 0;
    private static final int COPY_1_BYTE_OFFSET = 1;
    private static final int COPY_2_BYTE_OFFSET = 2;
    private static final int COPY_4_BYTE_OFFSET = 3;
    private static final FastThreadLocal<short[]> HASH_TABLE = new FastThreadLocal();
    private static final boolean DEFAULT_REUSE_HASHTABLE = SystemPropertyUtil.getBoolean("io.netty.handler.codec.compression.snappy.reuseHashTable", false);
    private final boolean reuseHashtable;
    private State state = State.READING_PREAMBLE;
    private byte tag;
    private int written;

    public Snappy() {
        this(DEFAULT_REUSE_HASHTABLE);
    }

    Snappy(boolean reuseHashtable) {
        this.reuseHashtable = reuseHashtable;
    }

    public static Snappy withHashTableReuse() {
        return new Snappy(true);
    }

    public void reset() {
        this.state = State.READING_PREAMBLE;
        this.tag = 0;
        this.written = 0;
    }

    public void encode(ByteBuf in, ByteBuf out, int length) {
        int inIndex;
        int b;
        int i = 0;
        while (true) {
            if (((b = length >>> i * 7) & 0xFFFFFF80) == 0) break;
            out.writeByte(b & 0x7F | 0x80);
            ++i;
        }
        out.writeByte(b);
        int baseIndex = inIndex = in.readerIndex();
        int hashTableSize = MathUtil.findNextPositivePowerOfTwo(length);
        hashTableSize = Math.min(hashTableSize, 16384);
        short[] table = this.getHashTable(hashTableSize);
        int shift = Integer.numberOfLeadingZeros(hashTableSize) + 1;
        int nextEmit = inIndex;
        if (length - inIndex >= 15) {
            int nextHash = Snappy.hash(in, ++inIndex, shift);
            block1: while (true) {
                int insertTail;
                int candidate;
                int skip = 32;
                int nextIndex = inIndex;
                do {
                    int bytesBetweenHashLookups;
                    inIndex = nextIndex;
                    int hash = nextHash;
                    if ((nextIndex = inIndex + (bytesBetweenHashLookups = skip++ >> 5)) > length - 4) break block1;
                    nextHash = Snappy.hash(in, nextIndex, shift);
                    candidate = baseIndex + (table[hash] & 0xFFFF);
                    table[hash] = (short)(inIndex - baseIndex);
                } while (in.getInt(inIndex) != in.getInt(candidate));
                Snappy.encodeLiteral(in, out, inIndex - nextEmit);
                do {
                    int base = inIndex;
                    int matched = 4 + Snappy.findMatchingLength(in, candidate + 4, inIndex + 4, length);
                    int offset = base - candidate;
                    Snappy.encodeCopy(out, offset, matched);
                    in.readerIndex(in.readerIndex() + matched);
                    insertTail = (inIndex += matched) - 1;
                    nextEmit = inIndex;
                    if (inIndex >= length - 4) break block1;
                    int prevHash = Snappy.hash(in, insertTail, shift);
                    table[prevHash] = (short)(inIndex - baseIndex - 1);
                    int currentHash = Snappy.hash(in, insertTail + 1, shift);
                    candidate = baseIndex + (table[currentHash] & 0xFFFF);
                    table[currentHash] = (short)(inIndex - baseIndex);
                } while (in.getInt(insertTail + 1) == in.getInt(candidate));
                nextHash = Snappy.hash(in, insertTail + 2, shift);
                ++inIndex;
            }
        }
        if (nextEmit < length) {
            Snappy.encodeLiteral(in, out, length - nextEmit);
        }
    }

    private static int hash(ByteBuf in, int index, int shift) {
        return in.getInt(index) * 506832829 >>> shift;
    }

    private short[] getHashTable(int hashTableSize) {
        if (this.reuseHashtable) {
            return Snappy.getHashTableFastThreadLocalArrayFill(hashTableSize);
        }
        return new short[hashTableSize];
    }

    public static short[] getHashTableFastThreadLocalArrayFill(int hashTableSize) {
        short[] hashTable = HASH_TABLE.get();
        if (hashTable == null || hashTable.length < hashTableSize) {
            hashTable = new short[hashTableSize];
            HASH_TABLE.set(hashTable);
            return hashTable;
        }
        Arrays.fill(hashTable, 0, hashTableSize, (short)0);
        return hashTable;
    }

    private static int findMatchingLength(ByteBuf in, int minIndex, int inIndex, int maxIndex) {
        int matched = 0;
        while (inIndex <= maxIndex - 4 && in.getInt(inIndex) == in.getInt(minIndex + matched)) {
            inIndex += 4;
            matched += 4;
        }
        while (inIndex < maxIndex && in.getByte(minIndex + matched) == in.getByte(inIndex)) {
            ++inIndex;
            ++matched;
        }
        return matched;
    }

    private static int bitsToEncode(int value) {
        int highestOneBit = Integer.highestOneBit(value);
        int bitLength = 0;
        while ((highestOneBit >>= 1) != 0) {
            ++bitLength;
        }
        return bitLength;
    }

    static void encodeLiteral(ByteBuf in, ByteBuf out, int length) {
        if (length < 61) {
            out.writeByte(length - 1 << 2);
        } else {
            int bitLength = Snappy.bitsToEncode(length - 1);
            int bytesToEncode = 1 + bitLength / 8;
            out.writeByte(59 + bytesToEncode << 2);
            for (int i = 0; i < bytesToEncode; ++i) {
                out.writeByte(length - 1 >> i * 8 & 0xFF);
            }
        }
        out.writeBytes(in, length);
    }

    private static void encodeCopyWithOffset(ByteBuf out, int offset, int length) {
        if (length < 12 && offset < 2048) {
            out.writeByte(1 | length - 4 << 2 | offset >> 8 << 5);
            out.writeByte(offset & 0xFF);
        } else {
            out.writeByte(2 | length - 1 << 2);
            out.writeByte(offset & 0xFF);
            out.writeByte(offset >> 8 & 0xFF);
        }
    }

    private static void encodeCopy(ByteBuf out, int offset, int length) {
        while (length >= 68) {
            Snappy.encodeCopyWithOffset(out, offset, 64);
            length -= 64;
        }
        if (length > 64) {
            Snappy.encodeCopyWithOffset(out, offset, 60);
            length -= 60;
        }
        Snappy.encodeCopyWithOffset(out, offset, length);
    }

    public void decode(ByteBuf in, ByteBuf out) {
        while (in.isReadable()) {
            block0 : switch (this.state) {
                case READING_PREAMBLE: {
                    int uncompressedLength = Snappy.readPreamble(in);
                    if (uncompressedLength == -1) {
                        return;
                    }
                    if (uncompressedLength == 0) {
                        return;
                    }
                    out.ensureWritable(uncompressedLength);
                    this.state = State.READING_TAG;
                }
                case READING_TAG: {
                    if (!in.isReadable()) {
                        return;
                    }
                    this.tag = in.readByte();
                    switch (this.tag & 3) {
                        case 0: {
                            this.state = State.READING_LITERAL;
                            break block0;
                        }
                        case 1: 
                        case 2: 
                        case 3: {
                            this.state = State.READING_COPY;
                        }
                    }
                    break;
                }
                case READING_LITERAL: {
                    int literalWritten = Snappy.decodeLiteral(this.tag, in, out);
                    if (literalWritten != -1) {
                        this.state = State.READING_TAG;
                        this.written += literalWritten;
                        break;
                    }
                    return;
                }
                case READING_COPY: {
                    switch (this.tag & 3) {
                        case 1: {
                            int decodeWritten = Snappy.decodeCopyWith1ByteOffset(this.tag, in, out, this.written);
                            if (decodeWritten != -1) {
                                this.state = State.READING_TAG;
                                this.written += decodeWritten;
                                break block0;
                            }
                            return;
                        }
                        case 2: {
                            int decodeWritten = Snappy.decodeCopyWith2ByteOffset(this.tag, in, out, this.written);
                            if (decodeWritten != -1) {
                                this.state = State.READING_TAG;
                                this.written += decodeWritten;
                                break block0;
                            }
                            return;
                        }
                        case 3: {
                            int decodeWritten = Snappy.decodeCopyWith4ByteOffset(this.tag, in, out, this.written);
                            if (decodeWritten != -1) {
                                this.state = State.READING_TAG;
                                this.written += decodeWritten;
                                break block0;
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    private static int readPreamble(ByteBuf in) {
        int length = 0;
        int byteIndex = 0;
        while (in.isReadable()) {
            short current = in.readUnsignedByte();
            length |= (current & 0x7F) << byteIndex++ * 7;
            if ((current & 0x80) == 0) {
                return length;
            }
            if (byteIndex < 4) continue;
            throw new DecompressionException("Preamble is greater than 4 bytes");
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int getPreamble(ByteBuf in) {
        if (this.state == State.READING_PREAMBLE) {
            int readerIndex = in.readerIndex();
            try {
                int n = Snappy.readPreamble(in);
                return n;
            }
            finally {
                in.readerIndex(readerIndex);
            }
        }
        return 0;
    }

    static int decodeLiteral(byte tag, ByteBuf in, ByteBuf out) {
        int length;
        in.markReaderIndex();
        switch (tag >> 2 & 0x3F) {
            case 60: {
                if (!in.isReadable()) {
                    return -1;
                }
                length = in.readUnsignedByte();
                break;
            }
            case 61: {
                if (in.readableBytes() < 2) {
                    return -1;
                }
                length = in.readUnsignedShortLE();
                break;
            }
            case 62: {
                if (in.readableBytes() < 3) {
                    return -1;
                }
                length = in.readUnsignedMediumLE();
                break;
            }
            case 63: {
                if (in.readableBytes() < 4) {
                    return -1;
                }
                length = in.readIntLE();
                break;
            }
            default: {
                length = tag >> 2 & 0x3F;
            }
        }
        if (in.readableBytes() < ++length) {
            in.resetReaderIndex();
            return -1;
        }
        out.writeBytes(in, length);
        return length;
    }

    private static int decodeCopyWith1ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
        if (!in.isReadable()) {
            return -1;
        }
        int initialIndex = out.writerIndex();
        int length = 4 + ((tag & 0x1C) >> 2);
        int offset = (tag & 0xE0) << 8 >> 5 | in.readUnsignedByte();
        Snappy.validateOffset(offset, writtenSoFar);
        out.markReaderIndex();
        if (offset < length) {
            for (int copies = length / offset; copies > 0; --copies) {
                out.readerIndex(initialIndex - offset);
                out.readBytes(out, offset);
            }
            if (length % offset != 0) {
                out.readerIndex(initialIndex - offset);
                out.readBytes(out, length % offset);
            }
        } else {
            out.readerIndex(initialIndex - offset);
            out.readBytes(out, length);
        }
        out.resetReaderIndex();
        return length;
    }

    private static int decodeCopyWith2ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
        if (in.readableBytes() < 2) {
            return -1;
        }
        int initialIndex = out.writerIndex();
        int length = 1 + (tag >> 2 & 0x3F);
        int offset = in.readUnsignedShortLE();
        Snappy.validateOffset(offset, writtenSoFar);
        out.markReaderIndex();
        if (offset < length) {
            for (int copies = length / offset; copies > 0; --copies) {
                out.readerIndex(initialIndex - offset);
                out.readBytes(out, offset);
            }
            if (length % offset != 0) {
                out.readerIndex(initialIndex - offset);
                out.readBytes(out, length % offset);
            }
        } else {
            out.readerIndex(initialIndex - offset);
            out.readBytes(out, length);
        }
        out.resetReaderIndex();
        return length;
    }

    private static int decodeCopyWith4ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
        if (in.readableBytes() < 4) {
            return -1;
        }
        int initialIndex = out.writerIndex();
        int length = 1 + (tag >> 2 & 0x3F);
        int offset = in.readIntLE();
        Snappy.validateOffset(offset, writtenSoFar);
        out.markReaderIndex();
        if (offset < length) {
            for (int copies = length / offset; copies > 0; --copies) {
                out.readerIndex(initialIndex - offset);
                out.readBytes(out, offset);
            }
            if (length % offset != 0) {
                out.readerIndex(initialIndex - offset);
                out.readBytes(out, length % offset);
            }
        } else {
            out.readerIndex(initialIndex - offset);
            out.readBytes(out, length);
        }
        out.resetReaderIndex();
        return length;
    }

    private static void validateOffset(int offset, int chunkSizeSoFar) {
        if (offset == 0) {
            throw new DecompressionException("Offset is less than minimum permissible value");
        }
        if (offset < 0) {
            throw new DecompressionException("Offset is greater than maximum value supported by this implementation");
        }
        if (offset > chunkSizeSoFar) {
            throw new DecompressionException("Offset exceeds size of chunk");
        }
    }

    static int calculateChecksum(ByteBuf data) {
        return Snappy.calculateChecksum(data, data.readerIndex(), data.readableBytes());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static int calculateChecksum(ByteBuf data, int offset, int length) {
        Crc32c crc32 = new Crc32c();
        try {
            crc32.update(data, offset, length);
            int n = Snappy.maskChecksum(crc32.getValue());
            return n;
        }
        finally {
            crc32.reset();
        }
    }

    static void validateChecksum(int expectedChecksum, ByteBuf data) {
        Snappy.validateChecksum(expectedChecksum, data, data.readerIndex(), data.readableBytes());
    }

    static void validateChecksum(int expectedChecksum, ByteBuf data, int offset, int length) {
        int actualChecksum = Snappy.calculateChecksum(data, offset, length);
        if (actualChecksum != expectedChecksum) {
            throw new DecompressionException("mismatching checksum: " + Integer.toHexString(actualChecksum) + " (expected: " + Integer.toHexString(expectedChecksum) + ')');
        }
    }

    static int maskChecksum(long checksum) {
        return (int)((checksum >> 15 | checksum << 17) + -1568478504L);
    }

    private static enum State {
        READING_PREAMBLE,
        READING_TAG,
        READING_LITERAL,
        READING_COPY;

    }
}

