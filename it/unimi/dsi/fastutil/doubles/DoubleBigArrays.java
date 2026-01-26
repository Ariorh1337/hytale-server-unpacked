/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public final class DoubleBigArrays {
    public static final double[][] EMPTY_BIG_ARRAY = new double[0][];
    public static final double[][] DEFAULT_EMPTY_BIG_ARRAY = new double[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int QUICKSORT_NO_REC = 7;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int MEDIUM = 40;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 8;
    private static final int RADIXSORT_NO_REC = 1024;

    private DoubleBigArrays() {
    }

    @Deprecated
    public static double get(double[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    @Deprecated
    public static void set(double[][] array, long index, double value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    @Deprecated
    public static void swap(double[][] array, long first, long second) {
        double t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    @Deprecated
    public static void add(double[][] array, long index, double incr) {
        double[] dArray = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        dArray[n] = dArray[n] + incr;
    }

    @Deprecated
    public static void mul(double[][] array, long index, double factor) {
        double[] dArray = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        dArray[n] = dArray[n] * factor;
    }

    @Deprecated
    public static void incr(double[][] array, long index) {
        double[] dArray = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        dArray[n] = dArray[n] + 1.0;
    }

    @Deprecated
    public static void decr(double[][] array, long index) {
        double[] dArray = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        dArray[n] = dArray[n] - 1.0;
    }

    @Deprecated
    public static long length(double[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    @Deprecated
    public static void copy(double[][] srcArray, long srcPos, double[][] destArray, long destPos, long length) {
        BigArrays.copy(srcArray, srcPos, destArray, destPos, length);
    }

    @Deprecated
    public static void copyFromBig(double[][] srcArray, long srcPos, double[] destArray, int destPos, int length) {
        BigArrays.copyFromBig(srcArray, srcPos, destArray, destPos, length);
    }

    @Deprecated
    public static void copyToBig(double[] srcArray, int srcPos, double[][] destArray, long destPos, long length) {
        BigArrays.copyToBig(srcArray, srcPos, destArray, destPos, length);
    }

    public static double[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        double[][] base = new double[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new double[0x8000000];
            }
            base[baseLength - 1] = new double[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new double[0x8000000];
            }
        }
        return base;
    }

    @Deprecated
    public static double[][] wrap(double[] array) {
        return BigArrays.wrap(array);
    }

    @Deprecated
    public static double[][] ensureCapacity(double[][] array, long length) {
        return DoubleBigArrays.ensureCapacity(array, length, DoubleBigArrays.length(array));
    }

    @Deprecated
    public static double[][] forceCapacity(double[][] array, long length, long preserve) {
        return BigArrays.forceCapacity(array, length, preserve);
    }

    @Deprecated
    public static double[][] ensureCapacity(double[][] array, long length, long preserve) {
        return length > DoubleBigArrays.length(array) ? DoubleBigArrays.forceCapacity(array, length, preserve) : array;
    }

    @Deprecated
    public static double[][] grow(double[][] array, long length) {
        long oldLength = DoubleBigArrays.length(array);
        return length > oldLength ? DoubleBigArrays.grow(array, length, oldLength) : array;
    }

    @Deprecated
    public static double[][] grow(double[][] array, long length, long preserve) {
        long oldLength = DoubleBigArrays.length(array);
        return length > oldLength ? DoubleBigArrays.ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
    }

    @Deprecated
    public static double[][] trim(double[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = DoubleBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        double[][] base = (double[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = DoubleArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    @Deprecated
    public static double[][] setLength(double[][] array, long length) {
        return BigArrays.setLength(array, length);
    }

    @Deprecated
    public static double[][] copy(double[][] array, long offset, long length) {
        return BigArrays.copy(array, offset, length);
    }

    @Deprecated
    public static double[][] copy(double[][] array) {
        return BigArrays.copy(array);
    }

    @Deprecated
    public static void fill(double[][] array, double value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    @Deprecated
    public static void fill(double[][] array, long from, long to, double value) {
        BigArrays.fill(array, from, to, value);
    }

    @Deprecated
    public static boolean equals(double[][] a1, double[][] a2) {
        return BigArrays.equals(a1, a2);
    }

    @Deprecated
    public static String toString(double[][] a) {
        return BigArrays.toString(a);
    }

    @Deprecated
    public static void ensureFromTo(double[][] a, long from, long to) {
        BigArrays.ensureFromTo(DoubleBigArrays.length(a), from, to);
    }

    @Deprecated
    public static void ensureOffsetLength(double[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(DoubleBigArrays.length(a), offset, length);
    }

    @Deprecated
    public static void ensureSameLength(double[][] a, double[][] b) {
        if (DoubleBigArrays.length(a) != DoubleBigArrays.length(b)) {
            throw new IllegalArgumentException("Array size mismatch: " + DoubleBigArrays.length(a) + " != " + DoubleBigArrays.length(b));
        }
    }

    private static ForkJoinPool getPool() {
        ForkJoinPool current = ForkJoinTask.getPool();
        return current == null ? ForkJoinPool.commonPool() : current;
    }

    private static void swap(double[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            BigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static long med3(double[][] x, long a, long b, long c, DoubleComparator comp) {
        int ab = comp.compare(BigArrays.get(x, a), BigArrays.get(x, b));
        int ac = comp.compare(BigArrays.get(x, a), BigArrays.get(x, c));
        int bc = comp.compare(BigArrays.get(x, b), BigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(double[][] a, long from, long to, DoubleComparator comp) {
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

    public static void quickSort(double[][] x, long from, long to, DoubleComparator comp) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            DoubleBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = DoubleBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = DoubleBigArrays.med3(x, m - s, m, m + s, comp);
                n = DoubleBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = DoubleBigArrays.med3(x, l, m, n, comp);
        }
        double v = BigArrays.get(x, m);
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
        DoubleBigArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        DoubleBigArrays.swap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            DoubleBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            DoubleBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static long med3(double[][] x, long a, long b, long c) {
        int ab = Double.compare(BigArrays.get(x, a), BigArrays.get(x, b));
        int ac = Double.compare(BigArrays.get(x, a), BigArrays.get(x, c));
        int bc = Double.compare(BigArrays.get(x, b), BigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(double[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (Double.compare(BigArrays.get(a, j), BigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            BigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(double[][] x, DoubleComparator comp) {
        DoubleBigArrays.quickSort(x, 0L, BigArrays.length(x), comp);
    }

    public static void quickSort(double[][] x, long from, long to) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            DoubleBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = DoubleBigArrays.med3(x, l, l + s, l + 2L * s);
                m = DoubleBigArrays.med3(x, m - s, m, m + s);
                n = DoubleBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = DoubleBigArrays.med3(x, l, m, n);
        }
        double v = BigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        while (true) {
            int comparison;
            if (b <= c && (comparison = Double.compare(BigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    BigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Double.compare(BigArrays.get(x, c), v)) >= 0) {
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
        DoubleBigArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        DoubleBigArrays.swap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            DoubleBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            DoubleBigArrays.quickSort(x, n - s, n);
        }
    }

    public static void quickSort(double[][] x) {
        DoubleBigArrays.quickSort(x, 0L, BigArrays.length(x));
    }

    public static void parallelQuickSort(double[][] x, long from, long to) {
        ForkJoinPool pool = DoubleBigArrays.getPool();
        if (to - from < 8192L || pool.getParallelism() == 1) {
            DoubleBigArrays.quickSort(x, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSort(x, from, to));
        }
    }

    public static void parallelQuickSort(double[][] x) {
        DoubleBigArrays.parallelQuickSort(x, 0L, BigArrays.length(x));
    }

    public static void parallelQuickSort(double[][] x, long from, long to, DoubleComparator comp) {
        ForkJoinPool pool = DoubleBigArrays.getPool();
        if (to - from < 8192L || pool.getParallelism() == 1) {
            DoubleBigArrays.quickSort(x, from, to, comp);
        } else {
            pool.invoke(new ForkJoinQuickSortComp(x, from, to, comp));
        }
    }

    public static void parallelQuickSort(double[][] x, DoubleComparator comp) {
        DoubleBigArrays.parallelQuickSort(x, 0L, BigArrays.length(x), comp);
    }

    public static long binarySearch(double[][] a, long from, long to, double key) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            double midVal = BigArrays.get(a, mid);
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

    public static long binarySearch(double[][] a, double key) {
        return DoubleBigArrays.binarySearch(a, 0L, BigArrays.length(a), key);
    }

    public static long binarySearch(double[][] a, long from, long to, double key, DoubleComparator c) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            double midVal = BigArrays.get(a, mid);
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

    public static long binarySearch(double[][] a, double key, DoubleComparator c) {
        return DoubleBigArrays.binarySearch(a, 0L, BigArrays.length(a), key, c);
    }

    private static final long fixDouble(double d) {
        long l = Double.doubleToRawLongBits(d);
        return l >= 0L ? l : l ^ Long.MAX_VALUE;
    }

    public static void radixSort(double[][] a) {
        DoubleBigArrays.radixSort(a, 0L, BigArrays.length(a));
    }

    public static void radixSort(double[][] a, long from, long to) {
        int maxLevel = 7;
        int stackSize = 1786;
        long[] offsetStack = new long[1786];
        int offsetPos = 0;
        long[] lengthStack = new long[1786];
        int lengthPos = 0;
        int[] levelStack = new int[1786];
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
            int n = signMask = (level = levelStack[--levelPos]) % 8 == 0 ? 128 : 0;
            if (length < 40L) {
                DoubleBigArrays.selectionSort(a, first, first + length);
                continue;
            }
            int shift = (7 - level % 8) * 8;
            long i = length;
            while (i-- != 0L) {
                BigArrays.set(digit, i, (byte)(DoubleBigArrays.fixDouble(BigArrays.get(a, first + i)) >>> shift & 0xFFL ^ (long)signMask));
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
                    if (level < 7 && count[i2] > 1L) {
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
                double t = BigArrays.get(a, i3 + first);
                c = BigArrays.get(digit, i3) & 0xFF;
                while (true) {
                    int n3 = c;
                    long l = pos[n3] - 1L;
                    pos[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    double z = t;
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

    private static void selectionSort(double[][] a, double[][] b, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (Double.compare(BigArrays.get(a, j), BigArrays.get(a, m)) >= 0 && (Double.compare(BigArrays.get(a, j), BigArrays.get(a, m)) != 0 || Double.compare(BigArrays.get(b, j), BigArrays.get(b, m)) >= 0)) continue;
                m = j;
            }
            if (m == i) continue;
            double t = BigArrays.get(a, i);
            BigArrays.set(a, i, BigArrays.get(a, m));
            BigArrays.set(a, m, t);
            t = BigArrays.get(b, i);
            BigArrays.set(b, i, BigArrays.get(b, m));
            BigArrays.set(b, m, t);
        }
    }

    public static void radixSort(double[][] a, double[][] b) {
        DoubleBigArrays.radixSort(a, b, 0L, BigArrays.length(a));
    }

    public static void radixSort(double[][] a, double[][] b, long from, long to) {
        int layers = 2;
        if (BigArrays.length(a) != BigArrays.length(b)) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        int maxLevel = 15;
        int stackSize = 3826;
        long[] offsetStack = new long[3826];
        int offsetPos = 0;
        long[] lengthStack = new long[3826];
        int lengthPos = 0;
        int[] levelStack = new int[3826];
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
            int n = signMask = (level = levelStack[--levelPos]) % 8 == 0 ? 128 : 0;
            if (length < 40L) {
                DoubleBigArrays.selectionSort(a, b, first, first + length);
                continue;
            }
            double[][] k = level < 8 ? a : b;
            int shift = (7 - level % 8) * 8;
            long i = length;
            while (i-- != 0L) {
                BigArrays.set(digit, i, (byte)(DoubleBigArrays.fixDouble(BigArrays.get(k, first + i)) >>> shift & 0xFFL ^ (long)signMask));
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
                    if (level < 15 && count[i2] > 1L) {
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
                double t = BigArrays.get(a, i3 + first);
                double u = BigArrays.get(b, i3 + first);
                c = BigArrays.get(digit, i3) & 0xFF;
                while (true) {
                    int n3 = c;
                    long l = pos[n3] - 1L;
                    pos[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    double z = t;
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

    private static void insertionSortIndirect(long[][] perm, double[][] a, double[][] b, long from, long to) {
        long i = from;
        while (++i < to) {
            long t = BigArrays.get(perm, i);
            long j = i;
            long u = BigArrays.get(perm, j - 1L);
            while (Double.compare(BigArrays.get(a, t), BigArrays.get(a, u)) < 0 || Double.compare(BigArrays.get(a, t), BigArrays.get(a, u)) == 0 && Double.compare(BigArrays.get(b, t), BigArrays.get(b, u)) < 0) {
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

    public static void radixSortIndirect(long[][] perm, double[][] a, double[][] b, boolean stable) {
        DoubleBigArrays.ensureSameLength(a, b);
        DoubleBigArrays.radixSortIndirect(perm, a, b, 0L, BigArrays.length(a), stable);
    }

    public static void radixSortIndirect(long[][] perm, double[][] a, double[][] b, long from, long to, boolean stable) {
        long[][] support;
        if (to - from < 1024L) {
            DoubleBigArrays.insertionSortIndirect(perm, a, b, from, to);
            return;
        }
        int layers = 2;
        int maxLevel = 15;
        int stackSize = 3826;
        int stackPos = 0;
        long[] offsetStack = new long[3826];
        long[] lengthStack = new long[3826];
        int[] levelStack = new int[3826];
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
            int signMask = level % 8 == 0 ? 128 : 0;
            double[][] k = level < 8 ? a : b;
            int shift = (7 - level % 8) * 8;
            long i = first + length;
            while (i-- != first) {
                int n = (int)(DoubleBigArrays.fixDouble(BigArrays.get(k, BigArrays.get(perm, i))) >>> shift & 0xFFL ^ (long)signMask);
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
                    int n = (int)(DoubleBigArrays.fixDouble(BigArrays.get(k, BigArrays.get(perm, i3))) >>> shift & 0xFFL ^ (long)signMask);
                    long l = pos[n] - 1L;
                    pos[n] = l;
                    BigArrays.set(support, l, BigArrays.get(perm, i3));
                }
                BigArrays.copy(support, 0L, perm, first, length);
                p = first;
                for (int i4 = 0; i4 < 256; ++i4) {
                    if (level < 15 && count[i4] > 1L) {
                        if (count[i4] < 1024L) {
                            DoubleBigArrays.insertionSortIndirect(perm, a, b, p, p + count[i4]);
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
                c = (int)(DoubleBigArrays.fixDouble(BigArrays.get(k, t)) >>> shift & 0xFFL ^ (long)signMask);
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
                        c = (int)(DoubleBigArrays.fixDouble(BigArrays.get(k, t)) >>> shift & 0xFFL ^ (long)signMask);
                    }
                    BigArrays.set(perm, i5, t);
                }
                if (level < 15 && count[c] > 1L) {
                    if (count[c] < 1024L) {
                        DoubleBigArrays.insertionSortIndirect(perm, a, b, i5, i5 + count[c]);
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

    public static double[][] shuffle(double[][] a, long from, long to, Random random) {
        return BigArrays.shuffle(a, from, to, random);
    }

    public static double[][] shuffle(double[][] a, Random random) {
        return BigArrays.shuffle(a, random);
    }

    protected static class ForkJoinQuickSort
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final long from;
        private final long to;
        private final double[][] x;

        public ForkJoinQuickSort(double[][] x, long from, long to) {
            this.from = from;
            this.to = to;
            this.x = x;
        }

        @Override
        protected void compute() {
            long c;
            long a;
            double[][] x = this.x;
            long len = this.to - this.from;
            if (len < 8192L) {
                DoubleBigArrays.quickSort(x, this.from, this.to);
                return;
            }
            long m = this.from + len / 2L;
            long l = this.from;
            long n = this.to - 1L;
            long s = len / 8L;
            l = DoubleBigArrays.med3(x, l, l + s, l + 2L * s);
            m = DoubleBigArrays.med3(x, m - s, m, m + s);
            n = DoubleBigArrays.med3(x, n - 2L * s, n - s, n);
            m = DoubleBigArrays.med3(x, l, m, n);
            double v = BigArrays.get(x, m);
            long b = a = this.from;
            long d = c = this.to - 1L;
            while (true) {
                int comparison;
                if (b <= c && (comparison = Double.compare(BigArrays.get(x, b), v)) <= 0) {
                    if (comparison == 0) {
                        BigArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = Double.compare(BigArrays.get(x, c), v)) >= 0) {
                    if (comparison == 0) {
                        BigArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                BigArrays.swap(x, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            DoubleBigArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1L);
            DoubleBigArrays.swap(x, b, this.to - s, s);
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
        private final double[][] x;
        private final DoubleComparator comp;

        public ForkJoinQuickSortComp(double[][] x, long from, long to, DoubleComparator comp) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.comp = comp;
        }

        @Override
        protected void compute() {
            long c;
            long a;
            double[][] x = this.x;
            long len = this.to - this.from;
            if (len < 8192L) {
                DoubleBigArrays.quickSort(x, this.from, this.to, this.comp);
                return;
            }
            long m = this.from + len / 2L;
            long l = this.from;
            long n = this.to - 1L;
            long s = len / 8L;
            l = DoubleBigArrays.med3(x, l, l + s, l + 2L * s, this.comp);
            m = DoubleBigArrays.med3(x, m - s, m, m + s, this.comp);
            n = DoubleBigArrays.med3(x, n - 2L * s, n - s, n, this.comp);
            m = DoubleBigArrays.med3(x, l, m, n, this.comp);
            double v = BigArrays.get(x, m);
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
            DoubleBigArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1L);
            DoubleBigArrays.swap(x, b, this.to - s, s);
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
    implements Hash.Strategy<double[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(double[][] o) {
            return Arrays.deepHashCode((Object[])o);
        }

        @Override
        public boolean equals(double[][] a, double[][] b) {
            return DoubleBigArrays.equals(a, b);
        }
    }
}

