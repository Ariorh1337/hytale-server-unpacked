/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public final class ByteBigArrays {
    public static final byte[][] EMPTY_BIG_ARRAY = new byte[0][];
    public static final byte[][] DEFAULT_EMPTY_BIG_ARRAY = new byte[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int QUICKSORT_NO_REC = 7;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int MEDIUM = 40;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 1;
    private static final int RADIXSORT_NO_REC = 1024;

    private ByteBigArrays() {
    }

    @Deprecated
    public static byte get(byte[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    @Deprecated
    public static void set(byte[][] array, long index, byte value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    @Deprecated
    public static void swap(byte[][] array, long first, long second) {
        byte t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    @Deprecated
    public static void add(byte[][] array, long index, byte incr) {
        byte[] byArray = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        byArray[n] = (byte)(byArray[n] + incr);
    }

    @Deprecated
    public static void mul(byte[][] array, long index, byte factor) {
        byte[] byArray = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        byArray[n] = (byte)(byArray[n] * factor);
    }

    @Deprecated
    public static void incr(byte[][] array, long index) {
        byte[] byArray = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        byArray[n] = (byte)(byArray[n] + 1);
    }

    @Deprecated
    public static void decr(byte[][] array, long index) {
        byte[] byArray = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        byArray[n] = (byte)(byArray[n] - 1);
    }

    @Deprecated
    public static long length(byte[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    @Deprecated
    public static void copy(byte[][] srcArray, long srcPos, byte[][] destArray, long destPos, long length) {
        BigArrays.copy(srcArray, srcPos, destArray, destPos, length);
    }

    @Deprecated
    public static void copyFromBig(byte[][] srcArray, long srcPos, byte[] destArray, int destPos, int length) {
        BigArrays.copyFromBig(srcArray, srcPos, destArray, destPos, length);
    }

    @Deprecated
    public static void copyToBig(byte[] srcArray, int srcPos, byte[][] destArray, long destPos, long length) {
        BigArrays.copyToBig(srcArray, srcPos, destArray, destPos, length);
    }

    public static byte[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        byte[][] base = new byte[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new byte[0x8000000];
            }
            base[baseLength - 1] = new byte[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new byte[0x8000000];
            }
        }
        return base;
    }

    @Deprecated
    public static byte[][] wrap(byte[] array) {
        return BigArrays.wrap(array);
    }

    @Deprecated
    public static byte[][] ensureCapacity(byte[][] array, long length) {
        return ByteBigArrays.ensureCapacity(array, length, ByteBigArrays.length(array));
    }

    @Deprecated
    public static byte[][] forceCapacity(byte[][] array, long length, long preserve) {
        return BigArrays.forceCapacity(array, length, preserve);
    }

    @Deprecated
    public static byte[][] ensureCapacity(byte[][] array, long length, long preserve) {
        return length > ByteBigArrays.length(array) ? ByteBigArrays.forceCapacity(array, length, preserve) : array;
    }

    @Deprecated
    public static byte[][] grow(byte[][] array, long length) {
        long oldLength = ByteBigArrays.length(array);
        return length > oldLength ? ByteBigArrays.grow(array, length, oldLength) : array;
    }

    @Deprecated
    public static byte[][] grow(byte[][] array, long length, long preserve) {
        long oldLength = ByteBigArrays.length(array);
        return length > oldLength ? ByteBigArrays.ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
    }

    @Deprecated
    public static byte[][] trim(byte[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = ByteBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        byte[][] base = (byte[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = ByteArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    @Deprecated
    public static byte[][] setLength(byte[][] array, long length) {
        return BigArrays.setLength(array, length);
    }

    @Deprecated
    public static byte[][] copy(byte[][] array, long offset, long length) {
        return BigArrays.copy(array, offset, length);
    }

    @Deprecated
    public static byte[][] copy(byte[][] array) {
        return BigArrays.copy(array);
    }

    @Deprecated
    public static void fill(byte[][] array, byte value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    @Deprecated
    public static void fill(byte[][] array, long from, long to, byte value) {
        BigArrays.fill(array, from, to, value);
    }

    @Deprecated
    public static boolean equals(byte[][] a1, byte[][] a2) {
        return BigArrays.equals(a1, a2);
    }

    @Deprecated
    public static String toString(byte[][] a) {
        return BigArrays.toString(a);
    }

    @Deprecated
    public static void ensureFromTo(byte[][] a, long from, long to) {
        BigArrays.ensureFromTo(ByteBigArrays.length(a), from, to);
    }

    @Deprecated
    public static void ensureOffsetLength(byte[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(ByteBigArrays.length(a), offset, length);
    }

    @Deprecated
    public static void ensureSameLength(byte[][] a, byte[][] b) {
        if (ByteBigArrays.length(a) != ByteBigArrays.length(b)) {
            throw new IllegalArgumentException("Array size mismatch: " + ByteBigArrays.length(a) + " != " + ByteBigArrays.length(b));
        }
    }

    private static ForkJoinPool getPool() {
        ForkJoinPool current = ForkJoinTask.getPool();
        return current == null ? ForkJoinPool.commonPool() : current;
    }

    private static void swap(byte[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            BigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static long med3(byte[][] x, long a, long b, long c, ByteComparator comp) {
        int ab = comp.compare(BigArrays.get(x, a), BigArrays.get(x, b));
        int ac = comp.compare(BigArrays.get(x, a), BigArrays.get(x, c));
        int bc = comp.compare(BigArrays.get(x, b), BigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(byte[][] a, long from, long to, ByteComparator comp) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (comp.compare(BigArrays.get(a, j), BigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            BigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(byte[][] x, long from, long to, ByteComparator comp) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            ByteBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = ByteBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = ByteBigArrays.med3(x, m - s, m, m + s, comp);
                n = ByteBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = ByteBigArrays.med3(x, l, m, n, comp);
        }
        byte v = BigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        while (true) {
            int comparison;
            if (b <= c && (comparison = comp.compare(BigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    BigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(BigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    BigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            BigArrays.swap(x, b++, c--);
        }
        long n = to;
        long s = Math.min(a - from, b - a);
        ByteBigArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        ByteBigArrays.swap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            ByteBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            ByteBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static long med3(byte[][] x, long a, long b, long c) {
        int ab = Byte.compare(BigArrays.get(x, a), BigArrays.get(x, b));
        int ac = Byte.compare(BigArrays.get(x, a), BigArrays.get(x, c));
        int bc = Byte.compare(BigArrays.get(x, b), BigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(byte[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (BigArrays.get(a, j) >= BigArrays.get(a, m)) continue;
                m = j;
            }
            if (m == i) continue;
            BigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(byte[][] x, ByteComparator comp) {
        ByteBigArrays.quickSort(x, 0L, BigArrays.length(x), comp);
    }

    public static void quickSort(byte[][] x, long from, long to) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            ByteBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = ByteBigArrays.med3(x, l, l + s, l + 2L * s);
                m = ByteBigArrays.med3(x, m - s, m, m + s);
                n = ByteBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = ByteBigArrays.med3(x, l, m, n);
        }
        byte v = BigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        while (true) {
            int comparison;
            if (b <= c && (comparison = Byte.compare(BigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    BigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Byte.compare(BigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    BigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            BigArrays.swap(x, b++, c--);
        }
        long n = to;
        long s = Math.min(a - from, b - a);
        ByteBigArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        ByteBigArrays.swap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            ByteBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            ByteBigArrays.quickSort(x, n - s, n);
        }
    }

    public static void quickSort(byte[][] x) {
        ByteBigArrays.quickSort(x, 0L, BigArrays.length(x));
    }

    public static void parallelQuickSort(byte[][] x, long from, long to) {
        ForkJoinPool pool = ByteBigArrays.getPool();
        if (to - from < 8192L || pool.getParallelism() == 1) {
            ByteBigArrays.quickSort(x, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSort(x, from, to));
        }
    }

    public static void parallelQuickSort(byte[][] x) {
        ByteBigArrays.parallelQuickSort(x, 0L, BigArrays.length(x));
    }

    public static void parallelQuickSort(byte[][] x, long from, long to, ByteComparator comp) {
        ForkJoinPool pool = ByteBigArrays.getPool();
        if (to - from < 8192L || pool.getParallelism() == 1) {
            ByteBigArrays.quickSort(x, from, to, comp);
        } else {
            pool.invoke(new ForkJoinQuickSortComp(x, from, to, comp));
        }
    }

    public static void parallelQuickSort(byte[][] x, ByteComparator comp) {
        ByteBigArrays.parallelQuickSort(x, 0L, BigArrays.length(x), comp);
    }

    public static long binarySearch(byte[][] a, long from, long to, byte key) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            byte midVal = BigArrays.get(a, mid);
            if (midVal < key) {
                from = mid + 1L;
                continue;
            }
            if (midVal > key) {
                to = mid - 1L;
                continue;
            }
            return mid;
        }
        return -(from + 1L);
    }

    public static long binarySearch(byte[][] a, byte key) {
        return ByteBigArrays.binarySearch(a, 0L, BigArrays.length(a), key);
    }

    public static long binarySearch(byte[][] a, long from, long to, byte key, ByteComparator c) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            byte midVal = BigArrays.get(a, mid);
            int cmp = c.compare(midVal, key);
            if (cmp < 0) {
                from = mid + 1L;
                continue;
            }
            if (cmp > 0) {
                to = mid - 1L;
                continue;
            }
            return mid;
        }
        return -(from + 1L);
    }

    public static long binarySearch(byte[][] a, byte key, ByteComparator c) {
        return ByteBigArrays.binarySearch(a, 0L, BigArrays.length(a), key, c);
    }

    public static void radixSort(byte[][] a) {
        ByteBigArrays.radixSort(a, 0L, BigArrays.length(a));
    }

    public static void radixSort(byte[][] a, long from, long to) {
        boolean maxLevel = false;
        boolean stackSize = true;
        long[] offsetStack = new long[1];
        int offsetPos = 0;
        long[] lengthStack = new long[1];
        int lengthPos = 0;
        int[] levelStack = new int[1];
        int levelPos = 0;
        offsetStack[offsetPos++] = from;
        lengthStack[lengthPos++] = to - from;
        levelStack[levelPos++] = 0;
        long[] count = new long[256];
        long[] pos = new long[256];
        byte[][] digit = ByteBigArrays.newBigArray(to - from);
        while (offsetPos > 0) {
            int level;
            int signMask;
            long first = offsetStack[--offsetPos];
            long length = lengthStack[--lengthPos];
            int n = signMask = (level = levelStack[--levelPos]) % 1 == 0 ? 128 : 0;
            if (length < 40L) {
                ByteBigArrays.selectionSort(a, first, first + length);
                continue;
            }
            int shift = (0 - level % 1) * 8;
            long i = length;
            while (i-- != 0L) {
                BigArrays.set(digit, i, (byte)(BigArrays.get(a, first + i) >>> shift & 0xFF ^ signMask));
            }
            i = length;
            while (i-- != 0L) {
                int n2 = BigArrays.get(digit, i) & 0xFF;
                count[n2] = count[n2] + 1L;
            }
            int lastUsed = -1;
            long p = 0L;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0L) {
                    lastUsed = i2;
                    if (level < 0 && count[i2] > 1L) {
                        offsetStack[offsetPos++] = p + first;
                        lengthStack[lengthPos++] = count[i2];
                        levelStack[levelPos++] = level + 1;
                    }
                }
                pos[i2] = p += count[i2];
            }
            long end = length - count[lastUsed];
            count[lastUsed] = 0L;
            int c = -1;
            for (long i3 = 0L; i3 < end; i3 += count[c]) {
                byte t = BigArrays.get(a, i3 + first);
                c = BigArrays.get(digit, i3) & 0xFF;
                while (true) {
                    int n3 = c;
                    long l = pos[n3] - 1L;
                    pos[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    byte z = t;
                    int zz = c;
                    t = BigArrays.get(a, d + first);
                    c = BigArrays.get(digit, d) & 0xFF;
                    BigArrays.set(a, d + first, z);
                    BigArrays.set(digit, d, (byte)zz);
                }
                BigArrays.set(a, i3 + first, t);
                count[c] = 0L;
            }
        }
    }

    private static void selectionSort(byte[][] a, byte[][] b, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (BigArrays.get(a, j) >= BigArrays.get(a, m) && (BigArrays.get(a, j) != BigArrays.get(a, m) || BigArrays.get(b, j) >= BigArrays.get(b, m))) continue;
                m = j;
            }
            if (m == i) continue;
            byte t = BigArrays.get(a, i);
            BigArrays.set(a, i, BigArrays.get(a, m));
            BigArrays.set(a, m, t);
            t = BigArrays.get(b, i);
            BigArrays.set(b, i, BigArrays.get(b, m));
            BigArrays.set(b, m, t);
        }
    }

    public static void radixSort(byte[][] a, byte[][] b) {
        ByteBigArrays.radixSort(a, b, 0L, BigArrays.length(a));
    }

    public static void radixSort(byte[][] a, byte[][] b, long from, long to) {
        int layers = 2;
        if (BigArrays.length(a) != BigArrays.length(b)) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        boolean maxLevel = true;
        int stackSize = 256;
        long[] offsetStack = new long[256];
        int offsetPos = 0;
        long[] lengthStack = new long[256];
        int lengthPos = 0;
        int[] levelStack = new int[256];
        int levelPos = 0;
        offsetStack[offsetPos++] = from;
        lengthStack[lengthPos++] = to - from;
        levelStack[levelPos++] = 0;
        long[] count = new long[256];
        long[] pos = new long[256];
        byte[][] digit = ByteBigArrays.newBigArray(to - from);
        while (offsetPos > 0) {
            int level;
            int signMask;
            long first = offsetStack[--offsetPos];
            long length = lengthStack[--lengthPos];
            int n = signMask = (level = levelStack[--levelPos]) % 1 == 0 ? 128 : 0;
            if (length < 40L) {
                ByteBigArrays.selectionSort(a, b, first, first + length);
                continue;
            }
            byte[][] k = level < 1 ? a : b;
            int shift = (0 - level % 1) * 8;
            long i = length;
            while (i-- != 0L) {
                BigArrays.set(digit, i, (byte)(BigArrays.get(k, first + i) >>> shift & 0xFF ^ signMask));
            }
            i = length;
            while (i-- != 0L) {
                int n2 = BigArrays.get(digit, i) & 0xFF;
                count[n2] = count[n2] + 1L;
            }
            int lastUsed = -1;
            long p = 0L;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0L) {
                    lastUsed = i2;
                    if (level < 1 && count[i2] > 1L) {
                        offsetStack[offsetPos++] = p + first;
                        lengthStack[lengthPos++] = count[i2];
                        levelStack[levelPos++] = level + 1;
                    }
                }
                pos[i2] = p += count[i2];
            }
            long end = length - count[lastUsed];
            count[lastUsed] = 0L;
            int c = -1;
            for (long i3 = 0L; i3 < end; i3 += count[c]) {
                byte t = BigArrays.get(a, i3 + first);
                byte u = BigArrays.get(b, i3 + first);
                c = BigArrays.get(digit, i3) & 0xFF;
                while (true) {
                    int n3 = c;
                    long l = pos[n3] - 1L;
                    pos[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    byte z = t;
                    int zz = c;
                    t = BigArrays.get(a, d + first);
                    BigArrays.set(a, d + first, z);
                    z = u;
                    u = BigArrays.get(b, d + first);
                    BigArrays.set(b, d + first, z);
                    c = BigArrays.get(digit, d) & 0xFF;
                    BigArrays.set(digit, d, (byte)zz);
                }
                BigArrays.set(a, i3 + first, t);
                BigArrays.set(b, i3 + first, u);
                count[c] = 0L;
            }
        }
    }

    private static void insertionSortIndirect(long[][] perm, byte[][] a, byte[][] b, long from, long to) {
        long i = from;
        while (++i < to) {
            long t = BigArrays.get(perm, i);
            long j = i;
            long u = BigArrays.get(perm, j - 1L);
            while (BigArrays.get(a, t) < BigArrays.get(a, u) || BigArrays.get(a, t) == BigArrays.get(a, u) && BigArrays.get(b, t) < BigArrays.get(b, u)) {
                BigArrays.set(perm, j, u);
                if (from == j - 1L) {
                    --j;
                    break;
                }
                u = BigArrays.get(perm, --j - 1L);
            }
            BigArrays.set(perm, j, t);
        }
    }

    public static void radixSortIndirect(long[][] perm, byte[][] a, byte[][] b, boolean stable) {
        ByteBigArrays.ensureSameLength(a, b);
        ByteBigArrays.radixSortIndirect(perm, a, b, 0L, BigArrays.length(a), stable);
    }

    public static void radixSortIndirect(long[][] perm, byte[][] a, byte[][] b, long from, long to, boolean stable) {
        long[][] support;
        if (to - from < 1024L) {
            ByteBigArrays.insertionSortIndirect(perm, a, b, from, to);
            return;
        }
        int layers = 2;
        boolean maxLevel = true;
        int stackSize = 256;
        int stackPos = 0;
        long[] offsetStack = new long[256];
        long[] lengthStack = new long[256];
        int[] levelStack = new int[256];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        long[] count = new long[256];
        long[] pos = new long[256];
        long[][] lArray = support = stable ? LongBigArrays.newBigArray(BigArrays.length(perm)) : null;
        while (stackPos > 0) {
            long first = offsetStack[--stackPos];
            long length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 1 == 0 ? 128 : 0;
            byte[][] k = level < 1 ? a : b;
            int shift = (0 - level % 1) * 8;
            long i = first + length;
            while (i-- != first) {
                int n = BigArrays.get(k, BigArrays.get(perm, i)) >>> shift & 0xFF ^ signMask;
                count[n] = count[n] + 1L;
            }
            int lastUsed = -1;
            long p = stable ? 0L : first;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0L) {
                    lastUsed = i2;
                }
                pos[i2] = p += count[i2];
            }
            if (stable) {
                long i3 = first + length;
                while (i3-- != first) {
                    int n = BigArrays.get(k, BigArrays.get(perm, i3)) >>> shift & 0xFF ^ signMask;
                    long l = pos[n] - 1L;
                    pos[n] = l;
                    BigArrays.set(support, l, BigArrays.get(perm, i3));
                }
                BigArrays.copy(support, 0L, perm, first, length);
                p = first;
                for (int i4 = 0; i4 < 256; ++i4) {
                    if (level < 1 && count[i4] > 1L) {
                        if (count[i4] < 1024L) {
                            ByteBigArrays.insertionSortIndirect(perm, a, b, p, p + count[i4]);
                        } else {
                            offsetStack[stackPos] = p;
                            lengthStack[stackPos] = count[i4];
                            levelStack[stackPos++] = level + 1;
                        }
                    }
                    p += count[i4];
                }
                Arrays.fill(count, 0L);
                continue;
            }
            long end = first + length - count[lastUsed];
            int c = -1;
            for (long i5 = first; i5 <= end; i5 += count[c]) {
                long t = BigArrays.get(perm, i5);
                c = BigArrays.get(k, t) >>> shift & 0xFF ^ signMask;
                if (i5 < end) {
                    while (true) {
                        int n = c;
                        long l = pos[n] - 1L;
                        pos[n] = l;
                        long d = l;
                        if (l <= i5) break;
                        long z = t;
                        t = BigArrays.get(perm, d);
                        BigArrays.set(perm, d, z);
                        c = BigArrays.get(k, t) >>> shift & 0xFF ^ signMask;
                    }
                    BigArrays.set(perm, i5, t);
                }
                if (level < 1 && count[c] > 1L) {
                    if (count[c] < 1024L) {
                        ByteBigArrays.insertionSortIndirect(perm, a, b, i5, i5 + count[c]);
                    } else {
                        offsetStack[stackPos] = i5;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0L;
            }
        }
    }

    public static byte[][] shuffle(byte[][] a, long from, long to, Random random) {
        return BigArrays.shuffle(a, from, to, random);
    }

    public static byte[][] shuffle(byte[][] a, Random random) {
        return BigArrays.shuffle(a, random);
    }

    protected static class ForkJoinQuickSort
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final long from;
        private final long to;
        private final byte[][] x;

        public ForkJoinQuickSort(byte[][] x, long from, long to) {
            this.from = from;
            this.to = to;
            this.x = x;
        }

        @Override
        protected void compute() {
            long c;
            long a;
            byte[][] x = this.x;
            long len = this.to - this.from;
            if (len < 8192L) {
                ByteBigArrays.quickSort(x, this.from, this.to);
                return;
            }
            long m = this.from + len / 2L;
            long l = this.from;
            long n = this.to - 1L;
            long s = len / 8L;
            l = ByteBigArrays.med3(x, l, l + s, l + 2L * s);
            m = ByteBigArrays.med3(x, m - s, m, m + s);
            n = ByteBigArrays.med3(x, n - 2L * s, n - s, n);
            m = ByteBigArrays.med3(x, l, m, n);
            byte v = BigArrays.get(x, m);
            long b = a = this.from;
            long d = c = this.to - 1L;
            while (true) {
                int comparison;
                if (b <= c && (comparison = Byte.compare(BigArrays.get(x, b), v)) <= 0) {
                    if (comparison == 0) {
                        BigArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = Byte.compare(BigArrays.get(x, c), v)) >= 0) {
                    if (comparison == 0) {
                        BigArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                BigArrays.swap(x, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            ByteBigArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1L);
            ByteBigArrays.swap(x, b, this.to - s, s);
            s = b - a;
            long t = d - c;
            if (s > 1L && t > 1L) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x, this.from, this.from + s), new ForkJoinQuickSort(x, this.to - t, this.to));
            } else if (s > 1L) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x, this.from, this.from + s));
            } else {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x, this.to - t, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSortComp
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final long from;
        private final long to;
        private final byte[][] x;
        private final ByteComparator comp;

        public ForkJoinQuickSortComp(byte[][] x, long from, long to, ByteComparator comp) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.comp = comp;
        }

        @Override
        protected void compute() {
            long c;
            long a;
            byte[][] x = this.x;
            long len = this.to - this.from;
            if (len < 8192L) {
                ByteBigArrays.quickSort(x, this.from, this.to, this.comp);
                return;
            }
            long m = this.from + len / 2L;
            long l = this.from;
            long n = this.to - 1L;
            long s = len / 8L;
            l = ByteBigArrays.med3(x, l, l + s, l + 2L * s, this.comp);
            m = ByteBigArrays.med3(x, m - s, m, m + s, this.comp);
            n = ByteBigArrays.med3(x, n - 2L * s, n - s, n, this.comp);
            m = ByteBigArrays.med3(x, l, m, n, this.comp);
            byte v = BigArrays.get(x, m);
            long b = a = this.from;
            long d = c = this.to - 1L;
            while (true) {
                int comparison;
                if (b <= c && (comparison = this.comp.compare(BigArrays.get(x, b), v)) <= 0) {
                    if (comparison == 0) {
                        BigArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = this.comp.compare(BigArrays.get(x, c), v)) >= 0) {
                    if (comparison == 0) {
                        BigArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                BigArrays.swap(x, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            ByteBigArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1L);
            ByteBigArrays.swap(x, b, this.to - s, s);
            s = b - a;
            long t = d - c;
            if (s > 1L && t > 1L) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp), new ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp));
            } else if (s > 1L) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp));
            } else {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp));
            }
        }
    }

    private static final class BigArrayHashStrategy
    implements Hash.Strategy<byte[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(byte[][] o) {
            return Arrays.deepHashCode((Object[])o);
        }

        @Override
        public boolean equals(byte[][] a, byte[][] b) {
            return ByteBigArrays.equals(a, b);
        }
    }
}

