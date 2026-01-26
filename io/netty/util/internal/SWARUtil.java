/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

public final class SWARUtil {
    public static long compilePattern(byte byteToFind) {
        return ((long)byteToFind & 0xFFL) * 0x101010101010101L;
    }

    public static long applyPattern(long word, long pattern) {
        long input = word ^ pattern;
        long tmp = (input & 0x7F7F7F7F7F7F7F7FL) + 0x7F7F7F7F7F7F7F7FL;
        return (tmp | input | 0x7F7F7F7F7F7F7F7FL) ^ 0xFFFFFFFFFFFFFFFFL;
    }

    public static int getIndex(long word, boolean isBigEndian) {
        int zeros = isBigEndian ? Long.numberOfLeadingZeros(word) : Long.numberOfTrailingZeros(word);
        return zeros >>> 3;
    }

    private static long applyUpperCasePattern(long word) {
        long rotated = word & 0x7F7F7F7F7F7F7F7FL;
        rotated += 0x2525252525252525L;
        rotated &= 0x7F7F7F7F7F7F7F7FL;
        rotated += 0x1A1A1A1A1A1A1A1AL;
        rotated &= word ^ 0xFFFFFFFFFFFFFFFFL;
        return rotated &= 0x8080808080808080L;
    }

    private static int applyUpperCasePattern(int word) {
        int rotated = word & 0x7F7F7F7F;
        rotated += 0x25252525;
        rotated &= 0x7F7F7F7F;
        rotated += 0x1A1A1A1A;
        rotated &= ~word;
        return rotated &= 0x80808080;
    }

    private static long applyLowerCasePattern(long word) {
        long rotated = word & 0x7F7F7F7F7F7F7F7FL;
        rotated += 0x505050505050505L;
        rotated &= 0x7F7F7F7F7F7F7F7FL;
        rotated += 0x1A1A1A1A1A1A1A1AL;
        rotated &= word ^ 0xFFFFFFFFFFFFFFFFL;
        return rotated &= 0x8080808080808080L;
    }

    private static int applyLowerCasePattern(int word) {
        int rotated = word & 0x7F7F7F7F;
        rotated += 0x5050505;
        rotated &= 0x7F7F7F7F;
        rotated += 0x1A1A1A1A;
        rotated &= ~word;
        return rotated &= 0x80808080;
    }

    public static boolean containsUpperCase(long word) {
        return SWARUtil.applyUpperCasePattern(word) != 0L;
    }

    public static boolean containsUpperCase(int word) {
        return SWARUtil.applyUpperCasePattern(word) != 0;
    }

    public static boolean containsLowerCase(long word) {
        return SWARUtil.applyLowerCasePattern(word) != 0L;
    }

    public static boolean containsLowerCase(int word) {
        return SWARUtil.applyLowerCasePattern(word) != 0;
    }

    public static long toLowerCase(long word) {
        long mask = SWARUtil.applyUpperCasePattern(word) >>> 2;
        return word | mask;
    }

    public static int toLowerCase(int word) {
        int mask = SWARUtil.applyUpperCasePattern(word) >>> 2;
        return word | mask;
    }

    public static long toUpperCase(long word) {
        long mask = SWARUtil.applyLowerCasePattern(word) >>> 2;
        return word & (mask ^ 0xFFFFFFFFFFFFFFFFL);
    }

    public static int toUpperCase(int word) {
        int mask = SWARUtil.applyLowerCasePattern(word) >>> 2;
        return word & ~mask;
    }

    private SWARUtil() {
    }
}

