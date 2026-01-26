/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Android;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Java8Compatibility;
import com.google.protobuf.UnsafeUtil;
import java.nio.ByteBuffer;
import java.util.Arrays;

final class Utf8 {
    private static final Processor processor = UnsafeProcessor.isAvailable() && !Android.isOnAndroidDevice() ? new UnsafeProcessor() : new SafeProcessor();
    private static final long ASCII_MASK_LONG = -9187201950435737472L;
    static final int MAX_BYTES_PER_CHAR = 3;
    static final int COMPLETE = 0;
    static final int MALFORMED = -1;
    private static final int UNSAFE_COUNT_ASCII_THRESHOLD = 16;

    static boolean isValidUtf8(byte[] bytes) {
        return processor.isValidUtf8(bytes, 0, bytes.length);
    }

    static boolean isValidUtf8(byte[] bytes, int index, int limit) {
        return processor.isValidUtf8(bytes, index, limit);
    }

    static int partialIsValidUtf8(int state, byte[] bytes, int index, int limit) {
        return processor.partialIsValidUtf8(state, bytes, index, limit);
    }

    private static int incompleteStateFor(int byte1) {
        return byte1 > -12 ? -1 : byte1;
    }

    private static int incompleteStateFor(int byte1, int byte2) {
        return byte1 > -12 || byte2 > -65 ? -1 : byte1 ^ byte2 << 8;
    }

    private static int incompleteStateFor(int byte1, int byte2, int byte3) {
        return byte1 > -12 || byte2 > -65 || byte3 > -65 ? -1 : byte1 ^ byte2 << 8 ^ byte3 << 16;
    }

    private static int incompleteStateFor(byte[] bytes, int index, int limit) {
        byte byte1 = bytes[index - 1];
        switch (limit - index) {
            case 0: {
                return Utf8.incompleteStateFor(byte1);
            }
            case 1: {
                return Utf8.incompleteStateFor(byte1, bytes[index]);
            }
            case 2: {
                return Utf8.incompleteStateFor(byte1, (int)bytes[index], (int)bytes[index + 1]);
            }
        }
        throw new AssertionError();
    }

    private static int incompleteStateFor(ByteBuffer buffer, int byte1, int index, int remaining) {
        switch (remaining) {
            case 0: {
                return Utf8.incompleteStateFor(byte1);
            }
            case 1: {
                return Utf8.incompleteStateFor(byte1, buffer.get(index));
            }
            case 2: {
                return Utf8.incompleteStateFor(byte1, (int)buffer.get(index), (int)buffer.get(index + 1));
            }
        }
        throw new AssertionError();
    }

    static int encodedLength(String string) {
        int i;
        int utf16Length;
        int utf8Length = utf16Length = string.length();
        for (i = 0; i < utf16Length && string.charAt(i) < '\u0080'; ++i) {
        }
        while (i < utf16Length) {
            char c = string.charAt(i);
            if (c < '\u0800') {
                utf8Length += 127 - c >>> 31;
            } else {
                utf8Length += Utf8.encodedLengthGeneral(string, i);
                break;
            }
            ++i;
        }
        if (utf8Length < utf16Length) {
            throw new IllegalArgumentException("UTF-8 length does not fit in int: " + ((long)utf8Length + 0x100000000L));
        }
        return utf8Length;
    }

    private static int encodedLengthGeneral(String string, int start) {
        int utf16Length = string.length();
        int utf8Length = 0;
        for (int i = start; i < utf16Length; ++i) {
            char c = string.charAt(i);
            if (c < '\u0800') {
                utf8Length += 127 - c >>> 31;
                continue;
            }
            utf8Length += 2;
            if ('\ud800' > c || c > '\udfff') continue;
            int cp = Character.codePointAt(string, i);
            if (cp < 65536) {
                throw new UnpairedSurrogateException(i, utf16Length);
            }
            ++i;
        }
        return utf8Length;
    }

    static int encode(String in, byte[] out, int offset, int length) {
        return processor.encodeUtf8(in, out, offset, length);
    }

    static boolean isValidUtf8(ByteBuffer buffer) {
        return processor.isValidUtf8(buffer, buffer.position(), buffer.remaining());
    }

    static int partialIsValidUtf8(int state, ByteBuffer buffer, int index, int limit) {
        return processor.partialIsValidUtf8(state, buffer, index, limit);
    }

    static String decodeUtf8(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
        return processor.decodeUtf8(buffer, index, size);
    }

    static String decodeUtf8(byte[] bytes, int index, int size) throws InvalidProtocolBufferException {
        return processor.decodeUtf8(bytes, index, size);
    }

    static void encodeUtf8(String in, ByteBuffer out) {
        processor.encodeUtf8(in, out);
    }

    private static int estimateConsecutiveAscii(ByteBuffer buffer, int index, int limit) {
        int i;
        int lim = limit - 7;
        for (i = index; i < lim && (buffer.getLong(i) & 0x8080808080808080L) == 0L; i += 8) {
        }
        return i - index;
    }

    private Utf8() {
    }

    static abstract class Processor {
        Processor() {
        }

        final boolean isValidUtf8(byte[] bytes, int index, int limit) {
            return this.partialIsValidUtf8(0, bytes, index, limit) == 0;
        }

        abstract int partialIsValidUtf8(int var1, byte[] var2, int var3, int var4);

        final boolean isValidUtf8(ByteBuffer buffer, int index, int limit) {
            return this.partialIsValidUtf8(0, buffer, index, limit) == 0;
        }

        final int partialIsValidUtf8(int state, ByteBuffer buffer, int index, int limit) {
            if (buffer.hasArray()) {
                int offset = buffer.arrayOffset();
                return this.partialIsValidUtf8(state, buffer.array(), offset + index, offset + limit);
            }
            if (buffer.isDirect()) {
                return this.partialIsValidUtf8Direct(state, buffer, index, limit);
            }
            return this.partialIsValidUtf8Default(state, buffer, index, limit);
        }

        abstract int partialIsValidUtf8Direct(int var1, ByteBuffer var2, int var3, int var4);

        final int partialIsValidUtf8Default(int state, ByteBuffer buffer, int index, int limit) {
            if (state != 0) {
                if (index >= limit) {
                    return state;
                }
                byte byte1 = (byte)state;
                if (byte1 < -32) {
                    if (byte1 < -62 || buffer.get(index++) > -65) {
                        return -1;
                    }
                } else if (byte1 < -16) {
                    byte byte2 = (byte)(~(state >> 8));
                    if (byte2 == 0) {
                        byte2 = buffer.get(index++);
                        if (index >= limit) {
                            return Utf8.incompleteStateFor(byte1, byte2);
                        }
                    }
                    if (byte2 > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || buffer.get(index++) > -65) {
                        return -1;
                    }
                } else {
                    byte byte2 = (byte)(~(state >> 8));
                    byte byte3 = 0;
                    if (byte2 == 0) {
                        byte2 = buffer.get(index++);
                        if (index >= limit) {
                            return Utf8.incompleteStateFor(byte1, byte2);
                        }
                    } else {
                        byte3 = (byte)(state >> 16);
                    }
                    if (byte3 == 0) {
                        byte3 = buffer.get(index++);
                        if (index >= limit) {
                            return Utf8.incompleteStateFor(byte1, byte2, byte3);
                        }
                    }
                    if (byte2 > -65 || (byte1 << 28) + (byte2 - -112) >> 30 != 0 || byte3 > -65 || buffer.get(index++) > -65) {
                        return -1;
                    }
                }
            }
            return Processor.partialIsValidUtf8(buffer, index, limit);
        }

        private static int partialIsValidUtf8(ByteBuffer buffer, int index, int limit) {
            index += Utf8.estimateConsecutiveAscii(buffer, index, limit);
            while (true) {
                byte byte2;
                byte byte1;
                if (index >= limit) {
                    return 0;
                }
                if ((byte1 = buffer.get(index++)) >= 0) continue;
                if (byte1 < -32) {
                    if (index >= limit) {
                        return byte1;
                    }
                    if (byte1 < -62 || buffer.get(index) > -65) {
                        return -1;
                    }
                    ++index;
                    continue;
                }
                if (byte1 < -16) {
                    if (index >= limit - 1) {
                        return Utf8.incompleteStateFor(buffer, byte1, index, limit - index);
                    }
                    if ((byte2 = buffer.get(index++)) > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || buffer.get(index) > -65) {
                        return -1;
                    }
                    ++index;
                    continue;
                }
                if (index >= limit - 2) {
                    return Utf8.incompleteStateFor(buffer, byte1, index, limit - index);
                }
                if ((byte2 = buffer.get(index++)) > -65 || (byte1 << 28) + (byte2 - -112) >> 30 != 0 || buffer.get(index++) > -65 || buffer.get(index++) > -65) break;
            }
            return -1;
        }

        abstract String decodeUtf8(byte[] var1, int var2, int var3) throws InvalidProtocolBufferException;

        final String decodeUtf8(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
            if (buffer.hasArray()) {
                int offset = buffer.arrayOffset();
                return this.decodeUtf8(buffer.array(), offset + index, size);
            }
            if (buffer.isDirect()) {
                return this.decodeUtf8Direct(buffer, index, size);
            }
            return this.decodeUtf8Default(buffer, index, size);
        }

        abstract String decodeUtf8Direct(ByteBuffer var1, int var2, int var3) throws InvalidProtocolBufferException;

        final String decodeUtf8Default(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
            byte b;
            int offset;
            if ((index | size | buffer.limit() - index - size) < 0) {
                throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", buffer.limit(), index, size));
            }
            int limit = offset + size;
            char[] resultArr = new char[size];
            int resultPos = 0;
            for (offset = index; offset < limit && DecodeUtil.isOneByte(b = buffer.get(offset)); ++offset) {
                DecodeUtil.handleOneByte(b, resultArr, resultPos++);
            }
            while (offset < limit) {
                byte byte1;
                if (DecodeUtil.isOneByte(byte1 = buffer.get(offset++))) {
                    byte b2;
                    DecodeUtil.handleOneByte(byte1, resultArr, resultPos++);
                    while (offset < limit && DecodeUtil.isOneByte(b2 = buffer.get(offset))) {
                        ++offset;
                        DecodeUtil.handleOneByte(b2, resultArr, resultPos++);
                    }
                    continue;
                }
                if (DecodeUtil.isTwoBytes(byte1)) {
                    if (offset >= limit) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    DecodeUtil.handleTwoBytes(byte1, buffer.get(offset++), resultArr, resultPos++);
                    continue;
                }
                if (DecodeUtil.isThreeBytes(byte1)) {
                    if (offset >= limit - 1) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    DecodeUtil.handleThreeBytes(byte1, buffer.get(offset++), buffer.get(offset++), resultArr, resultPos++);
                    continue;
                }
                if (offset >= limit - 2) {
                    throw InvalidProtocolBufferException.invalidUtf8();
                }
                DecodeUtil.handleFourBytes(byte1, buffer.get(offset++), buffer.get(offset++), buffer.get(offset++), resultArr, resultPos++);
                ++resultPos;
            }
            return new String(resultArr, 0, resultPos);
        }

        abstract int encodeUtf8(String var1, byte[] var2, int var3, int var4);

        final void encodeUtf8(String in, ByteBuffer out) {
            if (out.hasArray()) {
                int offset = out.arrayOffset();
                int endIndex = Utf8.encode(in, out.array(), offset + out.position(), out.remaining());
                Java8Compatibility.position(out, endIndex - offset);
            } else if (out.isDirect()) {
                this.encodeUtf8Direct(in, out);
            } else {
                this.encodeUtf8Default(in, out);
            }
        }

        abstract void encodeUtf8Direct(String var1, ByteBuffer var2);

        final void encodeUtf8Default(String in, ByteBuffer out) {
            int inLength = in.length();
            int outIx = out.position();
            try {
                char c;
                int inIx;
                for (inIx = 0; inIx < inLength && (c = in.charAt(inIx)) < '\u0080'; ++inIx) {
                    out.put(outIx + inIx, (byte)c);
                }
                if (inIx == inLength) {
                    Java8Compatibility.position(out, outIx + inIx);
                    return;
                }
                outIx += inIx;
                while (inIx < inLength) {
                    c = in.charAt(inIx);
                    if (c < '\u0080') {
                        out.put(outIx, (byte)c);
                    } else if (c < '\u0800') {
                        out.put(outIx++, (byte)(0xC0 | c >>> 6));
                        out.put(outIx, (byte)(0x80 | 0x3F & c));
                    } else if (c < '\ud800' || '\udfff' < c) {
                        out.put(outIx++, (byte)(0xE0 | c >>> 12));
                        out.put(outIx++, (byte)(0x80 | 0x3F & c >>> 6));
                        out.put(outIx, (byte)(0x80 | 0x3F & c));
                    } else {
                        char low;
                        if (inIx + 1 == inLength || !Character.isSurrogatePair(c, low = in.charAt(++inIx))) {
                            throw new UnpairedSurrogateException(inIx, inLength);
                        }
                        int codePoint = Character.toCodePoint(c, low);
                        out.put(outIx++, (byte)(0xF0 | codePoint >>> 18));
                        out.put(outIx++, (byte)(0x80 | 0x3F & codePoint >>> 12));
                        out.put(outIx++, (byte)(0x80 | 0x3F & codePoint >>> 6));
                        out.put(outIx, (byte)(0x80 | 0x3F & codePoint));
                    }
                    ++inIx;
                    ++outIx;
                }
                Java8Compatibility.position(out, outIx);
            }
            catch (IndexOutOfBoundsException e) {
                throw new ArrayIndexOutOfBoundsException("Not enough space in output buffer to encode UTF-8 string");
            }
        }
    }

    static class UnpairedSurrogateException
    extends IllegalArgumentException {
        UnpairedSurrogateException(int index, int length) {
            super("Unpaired surrogate at index " + index + " of " + length);
        }
    }

    static final class UnsafeProcessor
    extends Processor {
        UnsafeProcessor() {
        }

        static boolean isAvailable() {
            return UnsafeUtil.hasUnsafeArrayOperations() && UnsafeUtil.hasUnsafeByteBufferOperations();
        }

        @Override
        int partialIsValidUtf8(int state, byte[] bytes, int index, int limit) {
            if ((index | limit | bytes.length - limit) < 0) {
                throw new ArrayIndexOutOfBoundsException(String.format("Array length=%d, index=%d, limit=%d", bytes.length, index, limit));
            }
            long offset = index;
            long offsetLimit = limit;
            if (state != 0) {
                if (offset >= offsetLimit) {
                    return state;
                }
                byte byte1 = (byte)state;
                if (byte1 < -32) {
                    if (byte1 < -62 || UnsafeUtil.getByte(bytes, offset++) > -65) {
                        return -1;
                    }
                } else if (byte1 < -16) {
                    byte byte2 = (byte)(~(state >> 8));
                    if (byte2 == 0) {
                        byte2 = UnsafeUtil.getByte(bytes, offset++);
                        if (offset >= offsetLimit) {
                            return Utf8.incompleteStateFor(byte1, byte2);
                        }
                    }
                    if (byte2 > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || UnsafeUtil.getByte(bytes, offset++) > -65) {
                        return -1;
                    }
                } else {
                    byte byte2 = (byte)(~(state >> 8));
                    byte byte3 = 0;
                    if (byte2 == 0) {
                        byte2 = UnsafeUtil.getByte(bytes, offset++);
                        if (offset >= offsetLimit) {
                            return Utf8.incompleteStateFor(byte1, byte2);
                        }
                    } else {
                        byte3 = (byte)(state >> 16);
                    }
                    if (byte3 == 0) {
                        byte3 = UnsafeUtil.getByte(bytes, offset++);
                        if (offset >= offsetLimit) {
                            return Utf8.incompleteStateFor(byte1, byte2, byte3);
                        }
                    }
                    if (byte2 > -65 || (byte1 << 28) + (byte2 - -112) >> 30 != 0 || byte3 > -65 || UnsafeUtil.getByte(bytes, offset++) > -65) {
                        return -1;
                    }
                }
            }
            return UnsafeProcessor.partialIsValidUtf8(bytes, offset, (int)(offsetLimit - offset));
        }

        @Override
        int partialIsValidUtf8Direct(int state, ByteBuffer buffer, int index, int limit) {
            if ((index | limit | buffer.limit() - limit) < 0) {
                throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", buffer.limit(), index, limit));
            }
            long address = UnsafeUtil.addressOffset(buffer) + (long)index;
            long addressLimit = address + (long)(limit - index);
            if (state != 0) {
                if (address >= addressLimit) {
                    return state;
                }
                byte byte1 = (byte)state;
                if (byte1 < -32) {
                    if (byte1 < -62 || UnsafeUtil.getByte(address++) > -65) {
                        return -1;
                    }
                } else if (byte1 < -16) {
                    byte byte2 = (byte)(~(state >> 8));
                    if (byte2 == 0) {
                        byte2 = UnsafeUtil.getByte(address++);
                        if (address >= addressLimit) {
                            return Utf8.incompleteStateFor(byte1, byte2);
                        }
                    }
                    if (byte2 > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || UnsafeUtil.getByte(address++) > -65) {
                        return -1;
                    }
                } else {
                    byte byte2 = (byte)(~(state >> 8));
                    byte byte3 = 0;
                    if (byte2 == 0) {
                        byte2 = UnsafeUtil.getByte(address++);
                        if (address >= addressLimit) {
                            return Utf8.incompleteStateFor(byte1, byte2);
                        }
                    } else {
                        byte3 = (byte)(state >> 16);
                    }
                    if (byte3 == 0) {
                        byte3 = UnsafeUtil.getByte(address++);
                        if (address >= addressLimit) {
                            return Utf8.incompleteStateFor(byte1, byte2, byte3);
                        }
                    }
                    if (byte2 > -65 || (byte1 << 28) + (byte2 - -112) >> 30 != 0 || byte3 > -65 || UnsafeUtil.getByte(address++) > -65) {
                        return -1;
                    }
                }
            }
            return UnsafeProcessor.partialIsValidUtf8(address, (int)(addressLimit - address));
        }

        @Override
        String decodeUtf8(byte[] bytes, int index, int size) throws InvalidProtocolBufferException {
            String s = new String(bytes, index, size, Internal.UTF_8);
            if (s.indexOf(65533) < 0) {
                return s;
            }
            if (Arrays.equals(s.getBytes(Internal.UTF_8), Arrays.copyOfRange(bytes, index, index + size))) {
                return s;
            }
            throw InvalidProtocolBufferException.invalidUtf8();
        }

        @Override
        String decodeUtf8Direct(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
            byte b;
            long address;
            if ((index | size | buffer.limit() - index - size) < 0) {
                throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", buffer.limit(), index, size));
            }
            long addressLimit = address + (long)size;
            char[] resultArr = new char[size];
            int resultPos = 0;
            for (address = UnsafeUtil.addressOffset(buffer) + (long)index; address < addressLimit && DecodeUtil.isOneByte(b = UnsafeUtil.getByte(address)); ++address) {
                DecodeUtil.handleOneByte(b, resultArr, resultPos++);
            }
            while (address < addressLimit) {
                byte byte1;
                if (DecodeUtil.isOneByte(byte1 = UnsafeUtil.getByte(address++))) {
                    byte b2;
                    DecodeUtil.handleOneByte(byte1, resultArr, resultPos++);
                    while (address < addressLimit && DecodeUtil.isOneByte(b2 = UnsafeUtil.getByte(address))) {
                        ++address;
                        DecodeUtil.handleOneByte(b2, resultArr, resultPos++);
                    }
                    continue;
                }
                if (DecodeUtil.isTwoBytes(byte1)) {
                    if (address >= addressLimit) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    DecodeUtil.handleTwoBytes(byte1, UnsafeUtil.getByte(address++), resultArr, resultPos++);
                    continue;
                }
                if (DecodeUtil.isThreeBytes(byte1)) {
                    if (address >= addressLimit - 1L) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    DecodeUtil.handleThreeBytes(byte1, UnsafeUtil.getByte(address++), UnsafeUtil.getByte(address++), resultArr, resultPos++);
                    continue;
                }
                if (address >= addressLimit - 2L) {
                    throw InvalidProtocolBufferException.invalidUtf8();
                }
                DecodeUtil.handleFourBytes(byte1, UnsafeUtil.getByte(address++), UnsafeUtil.getByte(address++), UnsafeUtil.getByte(address++), resultArr, resultPos++);
                ++resultPos;
            }
            return new String(resultArr, 0, resultPos);
        }

        @Override
        int encodeUtf8(String in, byte[] out, int offset, int length) {
            char c;
            int inIx;
            long outIx = offset;
            long outLimit = outIx + (long)length;
            int inLimit = in.length();
            if (inLimit > length || out.length - length < offset) {
                throw new ArrayIndexOutOfBoundsException("Not enough space in output buffer to encode UTF-8 string");
            }
            for (inIx = 0; inIx < inLimit && (c = in.charAt(inIx)) < '\u0080'; ++inIx) {
                UnsafeUtil.putByte(out, outIx++, (byte)c);
            }
            if (inIx == inLimit) {
                return (int)outIx;
            }
            while (inIx < inLimit) {
                c = in.charAt(inIx);
                if (c < '\u0080' && outIx < outLimit) {
                    UnsafeUtil.putByte(out, outIx++, (byte)c);
                } else if (c < '\u0800' && outIx <= outLimit - 2L) {
                    UnsafeUtil.putByte(out, outIx++, (byte)(0x3C0 | c >>> 6));
                    UnsafeUtil.putByte(out, outIx++, (byte)(0x80 | 0x3F & c));
                } else if ((c < '\ud800' || '\udfff' < c) && outIx <= outLimit - 3L) {
                    UnsafeUtil.putByte(out, outIx++, (byte)(0x1E0 | c >>> 12));
                    UnsafeUtil.putByte(out, outIx++, (byte)(0x80 | 0x3F & c >>> 6));
                    UnsafeUtil.putByte(out, outIx++, (byte)(0x80 | 0x3F & c));
                } else if (outIx <= outLimit - 4L) {
                    char low;
                    if (inIx + 1 == inLimit || !Character.isSurrogatePair(c, low = in.charAt(++inIx))) {
                        throw new UnpairedSurrogateException(inIx - 1, inLimit);
                    }
                    int codePoint = Character.toCodePoint(c, low);
                    UnsafeUtil.putByte(out, outIx++, (byte)(0xF0 | codePoint >>> 18));
                    UnsafeUtil.putByte(out, outIx++, (byte)(0x80 | 0x3F & codePoint >>> 12));
                    UnsafeUtil.putByte(out, outIx++, (byte)(0x80 | 0x3F & codePoint >>> 6));
                    UnsafeUtil.putByte(out, outIx++, (byte)(0x80 | 0x3F & codePoint));
                } else {
                    if (!('\ud800' > c || c > '\udfff' || inIx + 1 != inLimit && Character.isSurrogatePair(c, in.charAt(inIx + 1)))) {
                        throw new UnpairedSurrogateException(inIx, inLimit);
                    }
                    throw new ArrayIndexOutOfBoundsException("Not enough space in output buffer to encode UTF-8 string");
                }
                ++inIx;
            }
            return (int)outIx;
        }

        @Override
        void encodeUtf8Direct(String in, ByteBuffer out) {
            char c;
            int inIx;
            long address = UnsafeUtil.addressOffset(out);
            long outIx = address + (long)out.position();
            long outLimit = address + (long)out.limit();
            int inLimit = in.length();
            if ((long)inLimit > outLimit - outIx) {
                throw new ArrayIndexOutOfBoundsException("Not enough space in output buffer to encode UTF-8 string");
            }
            for (inIx = 0; inIx < inLimit && (c = in.charAt(inIx)) < '\u0080'; ++inIx) {
                UnsafeUtil.putByte(outIx++, (byte)c);
            }
            if (inIx == inLimit) {
                Java8Compatibility.position(out, (int)(outIx - address));
                return;
            }
            while (inIx < inLimit) {
                c = in.charAt(inIx);
                if (c < '\u0080' && outIx < outLimit) {
                    UnsafeUtil.putByte(outIx++, (byte)c);
                } else if (c < '\u0800' && outIx <= outLimit - 2L) {
                    UnsafeUtil.putByte(outIx++, (byte)(0x3C0 | c >>> 6));
                    UnsafeUtil.putByte(outIx++, (byte)(0x80 | 0x3F & c));
                } else if ((c < '\ud800' || '\udfff' < c) && outIx <= outLimit - 3L) {
                    UnsafeUtil.putByte(outIx++, (byte)(0x1E0 | c >>> 12));
                    UnsafeUtil.putByte(outIx++, (byte)(0x80 | 0x3F & c >>> 6));
                    UnsafeUtil.putByte(outIx++, (byte)(0x80 | 0x3F & c));
                } else if (outIx <= outLimit - 4L) {
                    char low;
                    if (inIx + 1 == inLimit || !Character.isSurrogatePair(c, low = in.charAt(++inIx))) {
                        throw new UnpairedSurrogateException(inIx - 1, inLimit);
                    }
                    int codePoint = Character.toCodePoint(c, low);
                    UnsafeUtil.putByte(outIx++, (byte)(0xF0 | codePoint >>> 18));
                    UnsafeUtil.putByte(outIx++, (byte)(0x80 | 0x3F & codePoint >>> 12));
                    UnsafeUtil.putByte(outIx++, (byte)(0x80 | 0x3F & codePoint >>> 6));
                    UnsafeUtil.putByte(outIx++, (byte)(0x80 | 0x3F & codePoint));
                } else {
                    if (!('\ud800' > c || c > '\udfff' || inIx + 1 != inLimit && Character.isSurrogatePair(c, in.charAt(inIx + 1)))) {
                        throw new UnpairedSurrogateException(inIx, inLimit);
                    }
                    throw new ArrayIndexOutOfBoundsException("Not enough space in output buffer to encode UTF-8 string");
                }
                ++inIx;
            }
            Java8Compatibility.position(out, (int)(outIx - address));
        }

        private static int unsafeEstimateConsecutiveAscii(byte[] bytes, long offset, int maxChars) {
            int i;
            if (maxChars < 16) {
                return 0;
            }
            int unaligned = 8 - ((int)offset & 7);
            for (i = 0; i < unaligned; ++i) {
                if (UnsafeUtil.getByte(bytes, offset++) >= 0) continue;
                return i;
            }
            while (i + 8 <= maxChars && (UnsafeUtil.getLong((Object)bytes, UnsafeUtil.BYTE_ARRAY_BASE_OFFSET + offset) & 0x8080808080808080L) == 0L) {
                offset += 8L;
                i += 8;
            }
            while (i < maxChars) {
                if (UnsafeUtil.getByte(bytes, offset++) < 0) {
                    return i;
                }
                ++i;
            }
            return maxChars;
        }

        private static int unsafeEstimateConsecutiveAscii(long address, int maxChars) {
            int unaligned;
            int remaining = maxChars;
            if (remaining < 16) {
                return 0;
            }
            for (int j = unaligned = (int)(-address & 7L); j > 0; --j) {
                if (UnsafeUtil.getByte(address++) >= 0) continue;
                return unaligned - j;
            }
            remaining -= unaligned;
            while (remaining >= 8 && (UnsafeUtil.getLong(address) & 0x8080808080808080L) == 0L) {
                address += 8L;
                remaining -= 8;
            }
            return maxChars - remaining;
        }

        private static int partialIsValidUtf8(byte[] bytes, long offset, int remaining) {
            int skipped = UnsafeProcessor.unsafeEstimateConsecutiveAscii(bytes, offset, remaining);
            remaining -= skipped;
            offset += (long)skipped;
            while (true) {
                byte byte2;
                int byte1 = 0;
                while (remaining > 0) {
                    byte by = UnsafeUtil.getByte(bytes, offset++);
                    byte1 = by;
                    if (by < 0) break;
                    --remaining;
                }
                if (remaining == 0) {
                    return 0;
                }
                --remaining;
                if (byte1 < -32) {
                    if (remaining == 0) {
                        return byte1;
                    }
                    --remaining;
                    if (byte1 >= -62 && UnsafeUtil.getByte(bytes, offset++) <= -65) continue;
                    return -1;
                }
                if (byte1 < -16) {
                    if (remaining < 2) {
                        return UnsafeProcessor.unsafeIncompleteStateFor(bytes, byte1, offset, remaining);
                    }
                    remaining -= 2;
                    if (!((byte2 = UnsafeUtil.getByte(bytes, offset++)) > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96) && UnsafeUtil.getByte(bytes, offset++) <= -65) continue;
                    return -1;
                }
                if (remaining < 3) {
                    return UnsafeProcessor.unsafeIncompleteStateFor(bytes, byte1, offset, remaining);
                }
                remaining -= 3;
                if ((byte2 = UnsafeUtil.getByte(bytes, offset++)) > -65 || (byte1 << 28) + (byte2 - -112) >> 30 != 0 || UnsafeUtil.getByte(bytes, offset++) > -65 || UnsafeUtil.getByte(bytes, offset++) > -65) break;
            }
            return -1;
        }

        private static int partialIsValidUtf8(long address, int remaining) {
            int skipped = UnsafeProcessor.unsafeEstimateConsecutiveAscii(address, remaining);
            address += (long)skipped;
            remaining -= skipped;
            while (true) {
                byte byte2;
                int byte1 = 0;
                while (remaining > 0) {
                    byte by = UnsafeUtil.getByte(address++);
                    byte1 = by;
                    if (by < 0) break;
                    --remaining;
                }
                if (remaining == 0) {
                    return 0;
                }
                --remaining;
                if (byte1 < -32) {
                    if (remaining == 0) {
                        return byte1;
                    }
                    --remaining;
                    if (byte1 >= -62 && UnsafeUtil.getByte(address++) <= -65) continue;
                    return -1;
                }
                if (byte1 < -16) {
                    if (remaining < 2) {
                        return UnsafeProcessor.unsafeIncompleteStateFor(address, byte1, remaining);
                    }
                    remaining -= 2;
                    if (!((byte2 = UnsafeUtil.getByte(address++)) > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96) && UnsafeUtil.getByte(address++) <= -65) continue;
                    return -1;
                }
                if (remaining < 3) {
                    return UnsafeProcessor.unsafeIncompleteStateFor(address, byte1, remaining);
                }
                remaining -= 3;
                if ((byte2 = UnsafeUtil.getByte(address++)) > -65 || (byte1 << 28) + (byte2 - -112) >> 30 != 0 || UnsafeUtil.getByte(address++) > -65 || UnsafeUtil.getByte(address++) > -65) break;
            }
            return -1;
        }

        private static int unsafeIncompleteStateFor(byte[] bytes, int byte1, long offset, int remaining) {
            switch (remaining) {
                case 0: {
                    return Utf8.incompleteStateFor(byte1);
                }
                case 1: {
                    return Utf8.incompleteStateFor(byte1, UnsafeUtil.getByte(bytes, offset));
                }
                case 2: {
                    return Utf8.incompleteStateFor(byte1, UnsafeUtil.getByte(bytes, offset), UnsafeUtil.getByte(bytes, offset + 1L));
                }
            }
            throw new AssertionError();
        }

        private static int unsafeIncompleteStateFor(long address, int byte1, int remaining) {
            switch (remaining) {
                case 0: {
                    return Utf8.incompleteStateFor(byte1);
                }
                case 1: {
                    return Utf8.incompleteStateFor(byte1, UnsafeUtil.getByte(address));
                }
                case 2: {
                    return Utf8.incompleteStateFor(byte1, UnsafeUtil.getByte(address), UnsafeUtil.getByte(address + 1L));
                }
            }
            throw new AssertionError();
        }
    }

    static final class SafeProcessor
    extends Processor {
        SafeProcessor() {
        }

        @Override
        int partialIsValidUtf8(int state, byte[] bytes, int index, int limit) {
            if (state != 0) {
                if (index >= limit) {
                    return state;
                }
                byte byte1 = (byte)state;
                if (byte1 < -32) {
                    if (byte1 < -62 || bytes[index++] > -65) {
                        return -1;
                    }
                } else if (byte1 < -16) {
                    byte byte2 = (byte)(~(state >> 8));
                    if (byte2 == 0) {
                        byte2 = bytes[index++];
                        if (index >= limit) {
                            return Utf8.incompleteStateFor(byte1, byte2);
                        }
                    }
                    if (byte2 > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || bytes[index++] > -65) {
                        return -1;
                    }
                } else {
                    byte byte2 = (byte)(~(state >> 8));
                    byte byte3 = 0;
                    if (byte2 == 0) {
                        byte2 = bytes[index++];
                        if (index >= limit) {
                            return Utf8.incompleteStateFor(byte1, byte2);
                        }
                    } else {
                        byte3 = (byte)(state >> 16);
                    }
                    if (byte3 == 0) {
                        byte3 = bytes[index++];
                        if (index >= limit) {
                            return Utf8.incompleteStateFor(byte1, byte2, byte3);
                        }
                    }
                    if (byte2 > -65 || (byte1 << 28) + (byte2 - -112) >> 30 != 0 || byte3 > -65 || bytes[index++] > -65) {
                        return -1;
                    }
                }
            }
            return SafeProcessor.partialIsValidUtf8(bytes, index, limit);
        }

        @Override
        int partialIsValidUtf8Direct(int state, ByteBuffer buffer, int index, int limit) {
            return this.partialIsValidUtf8Default(state, buffer, index, limit);
        }

        @Override
        String decodeUtf8(byte[] bytes, int index, int size) throws InvalidProtocolBufferException {
            byte b;
            int offset;
            if ((index | size | bytes.length - index - size) < 0) {
                throw new ArrayIndexOutOfBoundsException(String.format("buffer length=%d, index=%d, size=%d", bytes.length, index, size));
            }
            int limit = offset + size;
            char[] resultArr = new char[size];
            int resultPos = 0;
            for (offset = index; offset < limit && DecodeUtil.isOneByte(b = bytes[offset]); ++offset) {
                DecodeUtil.handleOneByte(b, resultArr, resultPos++);
            }
            while (offset < limit) {
                byte byte1;
                if (DecodeUtil.isOneByte(byte1 = bytes[offset++])) {
                    byte b2;
                    DecodeUtil.handleOneByte(byte1, resultArr, resultPos++);
                    while (offset < limit && DecodeUtil.isOneByte(b2 = bytes[offset])) {
                        ++offset;
                        DecodeUtil.handleOneByte(b2, resultArr, resultPos++);
                    }
                    continue;
                }
                if (DecodeUtil.isTwoBytes(byte1)) {
                    if (offset >= limit) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    DecodeUtil.handleTwoBytes(byte1, bytes[offset++], resultArr, resultPos++);
                    continue;
                }
                if (DecodeUtil.isThreeBytes(byte1)) {
                    if (offset >= limit - 1) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    DecodeUtil.handleThreeBytes(byte1, bytes[offset++], bytes[offset++], resultArr, resultPos++);
                    continue;
                }
                if (offset >= limit - 2) {
                    throw InvalidProtocolBufferException.invalidUtf8();
                }
                DecodeUtil.handleFourBytes(byte1, bytes[offset++], bytes[offset++], bytes[offset++], resultArr, resultPos++);
                ++resultPos;
            }
            return new String(resultArr, 0, resultPos);
        }

        @Override
        String decodeUtf8Direct(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
            return this.decodeUtf8Default(buffer, index, size);
        }

        @Override
        int encodeUtf8(String in, byte[] out, int offset, int length) {
            char c;
            int i;
            int utf16Length = in.length();
            int j = offset;
            int limit = offset + length;
            for (i = 0; i < utf16Length && i + j < limit && (c = in.charAt(i)) < '\u0080'; ++i) {
                out[j + i] = (byte)c;
            }
            if (i == utf16Length) {
                return j + utf16Length;
            }
            j += i;
            while (i < utf16Length) {
                c = in.charAt(i);
                if (c < '\u0080' && j < limit) {
                    out[j++] = (byte)c;
                } else if (c < '\u0800' && j <= limit - 2) {
                    out[j++] = (byte)(0x3C0 | c >>> 6);
                    out[j++] = (byte)(0x80 | 0x3F & c);
                } else if ((c < '\ud800' || '\udfff' < c) && j <= limit - 3) {
                    out[j++] = (byte)(0x1E0 | c >>> 12);
                    out[j++] = (byte)(0x80 | 0x3F & c >>> 6);
                    out[j++] = (byte)(0x80 | 0x3F & c);
                } else if (j <= limit - 4) {
                    char low;
                    if (i + 1 == in.length() || !Character.isSurrogatePair(c, low = in.charAt(++i))) {
                        throw new UnpairedSurrogateException(i - 1, utf16Length);
                    }
                    int codePoint = Character.toCodePoint(c, low);
                    out[j++] = (byte)(0xF0 | codePoint >>> 18);
                    out[j++] = (byte)(0x80 | 0x3F & codePoint >>> 12);
                    out[j++] = (byte)(0x80 | 0x3F & codePoint >>> 6);
                    out[j++] = (byte)(0x80 | 0x3F & codePoint);
                } else {
                    if (!('\ud800' > c || c > '\udfff' || i + 1 != in.length() && Character.isSurrogatePair(c, in.charAt(i + 1)))) {
                        throw new UnpairedSurrogateException(i, utf16Length);
                    }
                    throw new ArrayIndexOutOfBoundsException("Not enough space in output buffer to encode UTF-8 string");
                }
                ++i;
            }
            return j;
        }

        @Override
        void encodeUtf8Direct(String in, ByteBuffer out) {
            this.encodeUtf8Default(in, out);
        }

        private static int partialIsValidUtf8(byte[] bytes, int index, int limit) {
            while (index < limit && bytes[index] >= 0) {
                ++index;
            }
            return index >= limit ? 0 : SafeProcessor.partialIsValidUtf8NonAscii(bytes, index, limit);
        }

        private static int partialIsValidUtf8NonAscii(byte[] bytes, int index, int limit) {
            while (true) {
                byte byte2;
                byte byte1;
                if (index >= limit) {
                    return 0;
                }
                if ((byte1 = bytes[index++]) >= 0) continue;
                if (byte1 < -32) {
                    if (index >= limit) {
                        return byte1;
                    }
                    if (byte1 >= -62 && bytes[index++] <= -65) continue;
                    return -1;
                }
                if (byte1 < -16) {
                    if (index >= limit - 1) {
                        return Utf8.incompleteStateFor(bytes, index, limit);
                    }
                    if (!((byte2 = bytes[index++]) > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96) && bytes[index++] <= -65) continue;
                    return -1;
                }
                if (index >= limit - 2) {
                    return Utf8.incompleteStateFor(bytes, index, limit);
                }
                if ((byte2 = bytes[index++]) > -65 || (byte1 << 28) + (byte2 - -112) >> 30 != 0 || bytes[index++] > -65 || bytes[index++] > -65) break;
            }
            return -1;
        }
    }

    private static class DecodeUtil {
        private DecodeUtil() {
        }

        private static boolean isOneByte(byte b) {
            return b >= 0;
        }

        private static boolean isTwoBytes(byte b) {
            return b < -32;
        }

        private static boolean isThreeBytes(byte b) {
            return b < -16;
        }

        private static void handleOneByte(byte byte1, char[] resultArr, int resultPos) {
            resultArr[resultPos] = (char)byte1;
        }

        private static void handleTwoBytes(byte byte1, byte byte2, char[] resultArr, int resultPos) throws InvalidProtocolBufferException {
            if (byte1 < -62 || DecodeUtil.isNotTrailingByte(byte2)) {
                throw InvalidProtocolBufferException.invalidUtf8();
            }
            resultArr[resultPos] = (char)((byte1 & 0x1F) << 6 | DecodeUtil.trailingByteValue(byte2));
        }

        private static void handleThreeBytes(byte byte1, byte byte2, byte byte3, char[] resultArr, int resultPos) throws InvalidProtocolBufferException {
            if (DecodeUtil.isNotTrailingByte(byte2) || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || DecodeUtil.isNotTrailingByte(byte3)) {
                throw InvalidProtocolBufferException.invalidUtf8();
            }
            resultArr[resultPos] = (char)((byte1 & 0xF) << 12 | DecodeUtil.trailingByteValue(byte2) << 6 | DecodeUtil.trailingByteValue(byte3));
        }

        private static void handleFourBytes(byte byte1, byte byte2, byte byte3, byte byte4, char[] resultArr, int resultPos) throws InvalidProtocolBufferException {
            if (DecodeUtil.isNotTrailingByte(byte2) || (byte1 << 28) + (byte2 - -112) >> 30 != 0 || DecodeUtil.isNotTrailingByte(byte3) || DecodeUtil.isNotTrailingByte(byte4)) {
                throw InvalidProtocolBufferException.invalidUtf8();
            }
            int codepoint = (byte1 & 7) << 18 | DecodeUtil.trailingByteValue(byte2) << 12 | DecodeUtil.trailingByteValue(byte3) << 6 | DecodeUtil.trailingByteValue(byte4);
            resultArr[resultPos] = DecodeUtil.highSurrogate(codepoint);
            resultArr[resultPos + 1] = DecodeUtil.lowSurrogate(codepoint);
        }

        private static boolean isNotTrailingByte(byte b) {
            return b > -65;
        }

        private static int trailingByteValue(byte b) {
            return b & 0x3F;
        }

        private static char highSurrogate(int codePoint) {
            return (char)(55232 + (codePoint >>> 10));
        }

        private static char lowSurrogate(int codePoint) {
            return (char)(56320 + (codePoint & 0x3FF));
        }
    }
}

