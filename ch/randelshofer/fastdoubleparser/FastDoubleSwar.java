/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

final class FastDoubleSwar {
    private static final VarHandle readLongLE = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle readIntLE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle readIntBE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle readLongBE = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);
    private static final ValueLayout.OfLong CHAR_ALIGNED_LONG = ValueLayout.OfLong.JAVA_LONG.withByteAlignment(2L);

    FastDoubleSwar() {
    }

    public static int countUpToEightDigitsUtf8(long chunk) {
        long val = chunk - 0x3030303030303030L;
        long predicate = (chunk + 0x4646464646464646L | val) & 0x8080808080808080L;
        return predicate == 0L ? 8 : Long.numberOfTrailingZeros(predicate) >> 3;
    }

    protected static boolean isDigit(char c) {
        return (char)(c - 48) < '\n';
    }

    protected static boolean isDigit(byte c) {
        return (char)(c - 48) < '\n';
    }

    public static boolean isEightDigits(byte[] a, int offset) {
        return FastDoubleSwar.isEightDigitsUtf8(FastDoubleSwar.readLongLE(a, offset));
    }

    public static boolean isEightDigits(char[] a, int offset) {
        MemorySegment seg = MemorySegment.ofArray(a);
        long first = seg.get(CHAR_ALIGNED_LONG, (long)offset << 1);
        long second = seg.get(CHAR_ALIGNED_LONG, ((long)offset << 1) + 8L);
        return FastDoubleSwar.isEightDigitsUtf16(first, second);
    }

    public static boolean isEightDigits(CharSequence a, int offset) {
        boolean success = true;
        for (int i = 0; i < 8; ++i) {
            char ch = a.charAt(i + offset);
            success &= FastDoubleSwar.isDigit(ch);
        }
        return success;
    }

    public static boolean isEightDigitsUtf16(long first, long second) {
        long fval = first - 0x30003000300030L;
        long fpre = first + 0x46004600460046L | fval;
        long sval = second - 0x30003000300030L;
        long spre = second + 0x46004600460046L | sval;
        return ((fpre | spre) & 0xFF80FF80FF80FF80L) == 0L;
    }

    public static boolean isEightDigitsUtf8(long chunk) {
        long val = chunk - 0x3030303030303030L;
        long predicate = (chunk + 0x4646464646464646L | val) & 0x8080808080808080L;
        return predicate == 0L;
    }

    public static boolean isEightZeroes(byte[] a, int offset) {
        return FastDoubleSwar.isEightZeroesUtf8(readLongLE.get(a, offset));
    }

    public static boolean isEightZeroes(CharSequence a, int offset) {
        boolean success = true;
        for (int i = 0; i < 8; ++i) {
            success &= '0' == a.charAt(i + offset);
        }
        return success;
    }

    public static boolean isEightZeroes(char[] a, int offset) {
        MemorySegment seg = MemorySegment.ofArray(a);
        long first = seg.get(CHAR_ALIGNED_LONG, (long)offset << 1);
        long second = seg.get(CHAR_ALIGNED_LONG, ((long)offset << 1) + 8L);
        return FastDoubleSwar.isEightZeroesUtf16(first, second);
    }

    public static boolean isEightZeroesUtf16(long first, long second) {
        return first == 0x30003000300030L && second == 0x30003000300030L;
    }

    public static boolean isEightZeroesUtf8(long chunk) {
        return chunk == 0x3030303030303030L;
    }

    public static int readIntBE(byte[] a, int offset) {
        return readIntBE.get(a, offset);
    }

    public static long readLongBE(byte[] a, int offset) {
        return readLongBE.get(a, offset);
    }

    public static long readLongLE(byte[] a, int offset) {
        return readLongLE.get(a, offset);
    }

    public static int tryToParseEightDigits(char[] a, int offset) {
        MemorySegment seg = MemorySegment.ofArray(a);
        long first = seg.get(CHAR_ALIGNED_LONG, (long)offset << 1);
        long second = seg.get(CHAR_ALIGNED_LONG, ((long)offset << 1) + 8L);
        return FastDoubleSwar.tryToParseEightDigitsUtf16(first, second);
    }

    public static int tryToParseEightDigits(byte[] a, int offset) {
        return FastDoubleSwar.tryToParseEightDigitsUtf8(readLongLE.get(a, offset));
    }

    public static int tryToParseEightDigits(CharSequence str, int offset) {
        long first = (long)str.charAt(offset) | (long)str.charAt(offset + 1) << 16 | (long)str.charAt(offset + 2) << 32 | (long)str.charAt(offset + 3) << 48;
        long second = (long)str.charAt(offset + 4) | (long)str.charAt(offset + 5) << 16 | (long)str.charAt(offset + 6) << 32 | (long)str.charAt(offset + 7) << 48;
        return FastDoubleSwar.tryToParseEightDigitsUtf16(first, second);
    }

    public static int tryToParseEightDigitsUtf16(long first, long second) {
        long fval = first - 0x30003000300030L;
        long fpre = first + 0x46004600460046L | fval;
        long sval = second - 0x30003000300030L;
        long spre = second + 0x46004600460046L | sval;
        if (((fpre | spre) & 0xFF80FF80FF80FF80L) != 0L) {
            return -1;
        }
        return (int)(sval * 281475406208040961L >>> 48) + (int)(fval * 281475406208040961L >>> 48) * 10000;
    }

    public static int tryToParseEightDigitsUtf8(byte[] a, int offset) {
        return FastDoubleSwar.tryToParseEightDigitsUtf8(FastDoubleSwar.readLongLE(a, offset));
    }

    public static int tryToParseEightDigitsUtf8(long chunk) {
        long val = chunk - 0x3030303030303030L;
        long predicate = (chunk + 0x4646464646464646L | val) & 0x8080808080808080L;
        if (predicate != 0L) {
            return -1;
        }
        long mask = 0xFF000000FFL;
        long mul1 = 4294967296000100L;
        long mul2 = 42949672960001L;
        val = val * 10L + (val >>> 8);
        val = (val & mask) * mul1 + (val >>> 16 & mask) * mul2 >>> 32;
        return (int)val;
    }

    public static long tryToParseEightHexDigits(CharSequence str, int offset) {
        long first = (long)str.charAt(offset) << 48 | (long)str.charAt(offset + 1) << 32 | (long)str.charAt(offset + 2) << 16 | (long)str.charAt(offset + 3);
        long second = (long)str.charAt(offset + 4) << 48 | (long)str.charAt(offset + 5) << 32 | (long)str.charAt(offset + 6) << 16 | (long)str.charAt(offset + 7);
        return FastDoubleSwar.tryToParseEightHexDigitsUtf16(first, second);
    }

    public static long tryToParseEightHexDigits(char[] chars, int offset) {
        long first = (long)chars[offset] << 48 | (long)chars[offset + 1] << 32 | (long)chars[offset + 2] << 16 | (long)chars[offset + 3];
        long second = (long)chars[offset + 4] << 48 | (long)chars[offset + 5] << 32 | (long)chars[offset + 6] << 16 | (long)chars[offset + 7];
        return FastDoubleSwar.tryToParseEightHexDigitsUtf16(first, second);
    }

    public static long tryToParseEightHexDigits(byte[] a, int offset) {
        return FastDoubleSwar.tryToParseEightHexDigitsUtf8(readLongBE.get(a, offset));
    }

    public static long tryToParseEightHexDigitsUtf16(long first, long second) {
        if (((first | second) & 0xFF80FF80FF80FF80L) != 0L) {
            return -1L;
        }
        long f = first * 65792L;
        long s = second * 65792L;
        long utf8Bytes = f & 0xFFFF000000000000L | (f & 0xFFFF0000L) << 16 | (s & 0xFFFF000000000000L) >>> 32 | (s & 0xFFFF0000L) >>> 16;
        return FastDoubleSwar.tryToParseEightHexDigitsUtf8(utf8Bytes);
    }

    public static long tryToParseEightHexDigitsUtf8(long chunk) {
        long lt_0 = chunk - 0x3030303030303030L;
        long gt_9 = chunk + 0x4646464646464646L;
        long vec = (chunk | 0x2020202020202020L) - 0x3030303030303030L;
        long ge_a = vec + 0x4F4F4F4F4F4F4F4FL;
        long le_f = vec - 0x3737373737373737L;
        if (((lt_0 &= 0x8080808080808080L) | (gt_9 &= 0x8080808080808080L)) != ((ge_a &= 0x8080808080808080L) & le_f)) {
            return -1L;
        }
        long gt_9mask = (gt_9 >>> 7) * 255L;
        long v = vec & (gt_9mask ^ 0xFFFFFFFFFFFFFFFFL) | vec - (0x2727272727272727L & gt_9mask);
        long v2 = v | v >>> 4;
        long v3 = v2 & 0xFF00FF00FF00FFL;
        long v4 = v3 | v3 >>> 8;
        long v5 = v4 >>> 16 & 0xFFFF0000L | v4 & 0xFFFFL;
        return v5;
    }

    public static int tryToParseFourDigits(char[] a, int offset) {
        MemorySegment seg = MemorySegment.ofArray(a);
        long first = seg.get(CHAR_ALIGNED_LONG, (long)(offset << 1));
        return FastDoubleSwar.tryToParseFourDigitsUtf16(first);
    }

    public static int tryToParseFourDigits(CharSequence str, int offset) {
        long first = (long)str.charAt(offset) | (long)str.charAt(offset + 1) << 16 | (long)str.charAt(offset + 2) << 32 | (long)str.charAt(offset + 3) << 48;
        return FastDoubleSwar.tryToParseFourDigitsUtf16(first);
    }

    public static int tryToParseFourDigits(byte[] a, int offset) {
        return FastDoubleSwar.tryToParseFourDigitsUtf8(readIntLE.get(a, offset));
    }

    public static int tryToParseFourDigitsUtf16(long first) {
        long fval = first - 0x30003000300030L;
        long fpre = first + 0x46004600460046L | fval;
        if ((fpre & 0xFF80FF80FF80FF80L) != 0L) {
            return -1;
        }
        return (int)(fval * 281475406208040961L >>> 48);
    }

    public static int tryToParseFourDigitsUtf8(int chunk) {
        int val = chunk - 0x30303030;
        int predicate = (chunk + 0x46464646 | val) & 0x80808080;
        if ((long)predicate != 0L) {
            return -1;
        }
        val = val * 2561 >>> 8;
        val = (val & 0xFF) * 100 + ((val & 0xFF0000) >> 16);
        return val;
    }

    public static int tryToParseUpTo7Digits(byte[] str, int from, int to) {
        int result = 0;
        boolean success = true;
        while (from < to) {
            byte ch = str[from];
            char digit = (char)(ch - 48);
            success &= digit < '\n';
            result = 10 * result + digit;
            ++from;
        }
        return success ? result : -1;
    }

    public static int tryToParseUpTo7Digits(char[] str, int from, int to) {
        int result = 0;
        boolean success = true;
        while (from < to) {
            char ch = str[from];
            char digit = (char)(ch - 48);
            success &= digit < '\n';
            result = 10 * result + digit;
            ++from;
        }
        return success ? result : -1;
    }

    public static int tryToParseUpTo7Digits(CharSequence str, int from, int to) {
        int result = 0;
        boolean success = true;
        while (from < to) {
            char ch = str.charAt(from);
            char digit = (char)(ch - 48);
            success &= digit < '\n';
            result = 10 * result + digit;
            ++from;
        }
        return success ? result : -1;
    }

    public static void writeIntBE(byte[] a, int offset, int value) {
        readIntBE.set(a, offset, value);
    }

    public static void writeLongBE(byte[] a, int offset, long value) {
        readLongBE.set(a, offset, value);
    }

    public static double fma(double a, double b, double c) {
        return Math.fma(a, b, c);
    }
}

