/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanComparator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public final class BooleanBigArrays {
    public static final boolean[][] EMPTY_BIG_ARRAY = new boolean[0][];
    public static final boolean[][] DEFAULT_EMPTY_BIG_ARRAY = new boolean[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int QUICKSORT_NO_REC = 7;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int MEDIUM = 40;

    private BooleanBigArrays() {
    }

    @Deprecated
    public static boolean get(boolean[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    @Deprecated
    public static void set(boolean[][] array, long index, boolean value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    @Deprecated
    public static void swap(boolean[][] array, long first, long second) {
        boolean t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    @Deprecated
    public static long length(boolean[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    @Deprecated
    public static void copy(boolean[][] srcArray, long srcPos, boolean[][] destArray, long destPos, long length) {
        BigArrays.copy(srcArray, srcPos, destArray, destPos, length);
    }

    @Deprecated
    public static void copyFromBig(boolean[][] srcArray, long srcPos, boolean[] destArray, int destPos, int length) {
        BigArrays.copyFromBig(srcArray, srcPos, destArray, destPos, length);
    }

    @Deprecated
    public static void copyToBig(boolean[] srcArray, int srcPos, boolean[][] destArray, long destPos, long length) {
        BigArrays.copyToBig(srcArray, srcPos, destArray, destPos, length);
    }

    public static boolean[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        boolean[][] base = new boolean[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new boolean[0x8000000];
            }
            base[baseLength - 1] = new boolean[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new boolean[0x8000000];
            }
        }
        return base;
    }

    @Deprecated
    public static boolean[][] wrap(boolean[] array) {
        return BigArrays.wrap(array);
    }

    @Deprecated
    public static boolean[][] ensureCapacity(boolean[][] array, long length) {
        return BooleanBigArrays.ensureCapacity(array, length, BooleanBigArrays.length(array));
    }

    @Deprecated
    public static boolean[][] forceCapacity(boolean[][] array, long length, long preserve) {
        return BigArrays.forceCapacity(array, length, preserve);
    }

    @Deprecated
    public static boolean[][] ensureCapacity(boolean[][] array, long length, long preserve) {
        return length > BooleanBigArrays.length(array) ? BooleanBigArrays.forceCapacity(array, length, preserve) : array;
    }

    @Deprecated
    public static boolean[][] grow(boolean[][] array, long length) {
        long oldLength = BooleanBigArrays.length(array);
        return length > oldLength ? BooleanBigArrays.grow(array, length, oldLength) : array;
    }

    @Deprecated
    public static boolean[][] grow(boolean[][] array, long length, long preserve) {
        long oldLength = BooleanBigArrays.length(array);
        return length > oldLength ? BooleanBigArrays.ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
    }

    @Deprecated
    public static boolean[][] trim(boolean[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = BooleanBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        boolean[][] base = (boolean[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = BooleanArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    @Deprecated
    public static boolean[][] setLength(boolean[][] array, long length) {
        return BigArrays.setLength(array, length);
    }

    @Deprecated
    public static boolean[][] copy(boolean[][] array, long offset, long length) {
        return BigArrays.copy(array, offset, length);
    }

    @Deprecated
    public static boolean[][] copy(boolean[][] array) {
        return BigArrays.copy(array);
    }

    @Deprecated
    public static void fill(boolean[][] array, boolean value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    @Deprecated
    public static void fill(boolean[][] array, long from, long to, boolean value) {
        BigArrays.fill(array, from, to, value);
    }

    @Deprecated
    public static boolean equals(boolean[][] a1, boolean[][] a2) {
        return BigArrays.equals(a1, a2);
    }

    @Deprecated
    public static String toString(boolean[][] a) {
        return BigArrays.toString(a);
    }

    @Deprecated
    public static void ensureFromTo(boolean[][] a, long from, long to) {
        BigArrays.ensureFromTo(BooleanBigArrays.length(a), from, to);
    }

    @Deprecated
    public static void ensureOffsetLength(boolean[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(BooleanBigArrays.length(a), offset, length);
    }

    @Deprecated
    public static void ensureSameLength(boolean[][] a, boolean[][] b) {
        if (BooleanBigArrays.length(a) != BooleanBigArrays.length(b)) {
            throw new IllegalArgumentException("Array size mismatch: " + BooleanBigArrays.length(a) + " != " + BooleanBigArrays.length(b));
        }
    }

    private static ForkJoinPool getPool() {
        ForkJoinPool current = ForkJoinTask.getPool();
        return current == null ? ForkJoinPool.commonPool() : current;
    }

    private static void swap(boolean[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            BigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static long med3(boolean[][] x, long a, long b, long c, BooleanComparator comp) {
        int ab = comp.compare(BigArrays.get(x, a), BigArrays.get(x, b));
        int ac = comp.compare(BigArrays.get(x, a), BigArrays.get(x, c));
        int bc = comp.compare(BigArrays.get(x, b), BigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(boolean[][] a, long from, long to, BooleanComparator comp) {
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

    public static void quickSort(boolean[][] x, long from, long to, BooleanComparator comp) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            BooleanBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = BooleanBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = BooleanBigArrays.med3(x, m - s, m, m + s, comp);
                n = BooleanBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = BooleanBigArrays.med3(x, l, m, n, comp);
        }
        boolean v = BigArrays.get(x, m);
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
        BooleanBigArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        BooleanBigArrays.swap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            BooleanBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            BooleanBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static long med3(boolean[][] x, long a, long b, long c) {
        int ab = Boolean.compare(BigArrays.get(x, a), BigArrays.get(x, b));
        int ac = Boolean.compare(BigArrays.get(x, a), BigArrays.get(x, c));
        int bc = Boolean.compare(BigArrays.get(x, b), BigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(boolean[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (BigArrays.get(a, j) || !BigArrays.get(a, m)) continue;
                m = j;
            }
            if (m == i) continue;
            BigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(boolean[][] x, BooleanComparator comp) {
        BooleanBigArrays.quickSort(x, 0L, BigArrays.length(x), comp);
    }

    public static void quickSort(boolean[][] x, long from, long to) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            BooleanBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = BooleanBigArrays.med3(x, l, l + s, l + 2L * s);
                m = BooleanBigArrays.med3(x, m - s, m, m + s);
                n = BooleanBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = BooleanBigArrays.med3(x, l, m, n);
        }
        boolean v = BigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        while (true) {
            int comparison;
            if (b <= c && (comparison = Boolean.compare(BigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    BigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Boolean.compare(BigArrays.get(x, c), v)) >= 0) {
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
        BooleanBigArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        BooleanBigArrays.swap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            BooleanBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            BooleanBigArrays.quickSort(x, n - s, n);
        }
    }

    public static void quickSort(boolean[][] x) {
        BooleanBigArrays.quickSort(x, 0L, BigArrays.length(x));
    }

    public static void parallelQuickSort(boolean[][] x, long from, long to) {
        ForkJoinPool pool = BooleanBigArrays.getPool();
        if (to - from < 8192L || pool.getParallelism() == 1) {
            BooleanBigArrays.quickSort(x, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSort(x, from, to));
        }
    }

    public static void parallelQuickSort(boolean[][] x) {
        BooleanBigArrays.parallelQuickSort(x, 0L, BigArrays.length(x));
    }

    public static void parallelQuickSort(boolean[][] x, long from, long to, BooleanComparator comp) {
        ForkJoinPool pool = BooleanBigArrays.getPool();
        if (to - from < 8192L || pool.getParallelism() == 1) {
            BooleanBigArrays.quickSort(x, from, to, comp);
        } else {
            pool.invoke(new ForkJoinQuickSortComp(x, from, to, comp));
        }
    }

    public static void parallelQuickSort(boolean[][] x, BooleanComparator comp) {
        BooleanBigArrays.parallelQuickSort(x, 0L, BigArrays.length(x), comp);
    }

    public static boolean[][] shuffle(boolean[][] a, long from, long to, Random random) {
        return BigArrays.shuffle(a, from, to, random);
    }

    public static boolean[][] shuffle(boolean[][] a, Random random) {
        return BigArrays.shuffle(a, random);
    }

    protected static class ForkJoinQuickSort
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final long from;
        private final long to;
        private final boolean[][] x;

        public ForkJoinQuickSort(boolean[][] x, long from, long to) {
            this.from = from;
            this.to = to;
            this.x = x;
        }

        @Override
        protected void compute() {
            long c;
            long a;
            boolean[][] x = this.x;
            long len = this.to - this.from;
            if (len < 8192L) {
                BooleanBigArrays.quickSort(x, this.from, this.to);
                return;
            }
            long m = this.from + len / 2L;
            long l = this.from;
            long n = this.to - 1L;
            long s = len / 8L;
            l = BooleanBigArrays.med3(x, l, l + s, l + 2L * s);
            m = BooleanBigArrays.med3(x, m - s, m, m + s);
            n = BooleanBigArrays.med3(x, n - 2L * s, n - s, n);
            m = BooleanBigArrays.med3(x, l, m, n);
            boolean v = BigArrays.get(x, m);
            long b = a = this.from;
            long d = c = this.to - 1L;
            while (true) {
                int comparison;
                if (b <= c && (comparison = Boolean.compare(BigArrays.get(x, b), v)) <= 0) {
                    if (comparison == 0) {
                        BigArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = Boolean.compare(BigArrays.get(x, c), v)) >= 0) {
                    if (comparison == 0) {
                        BigArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                BigArrays.swap(x, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            BooleanBigArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1L);
            BooleanBigArrays.swap(x, b, this.to - s, s);
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
        private final boolean[][] x;
        private final BooleanComparator comp;

        public ForkJoinQuickSortComp(boolean[][] x, long from, long to, BooleanComparator comp) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.comp = comp;
        }

        @Override
        protected void compute() {
            long c;
            long a;
            boolean[][] x = this.x;
            long len = this.to - this.from;
            if (len < 8192L) {
                BooleanBigArrays.quickSort(x, this.from, this.to, this.comp);
                return;
            }
            long m = this.from + len / 2L;
            long l = this.from;
            long n = this.to - 1L;
            long s = len / 8L;
            l = BooleanBigArrays.med3(x, l, l + s, l + 2L * s, this.comp);
            m = BooleanBigArrays.med3(x, m - s, m, m + s, this.comp);
            n = BooleanBigArrays.med3(x, n - 2L * s, n - s, n, this.comp);
            m = BooleanBigArrays.med3(x, l, m, n, this.comp);
            boolean v = BigArrays.get(x, m);
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
            BooleanBigArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1L);
            BooleanBigArrays.swap(x, b, this.to - s, s);
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
    implements Hash.Strategy<boolean[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(boolean[][] o) {
            return Arrays.deepHashCode((Object[])o);
        }

        @Override
        public boolean equals(boolean[][] a, boolean[][] b) {
            return BooleanBigArrays.equals(a, b);
        }
    }
}

