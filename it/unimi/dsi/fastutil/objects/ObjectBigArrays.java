/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public final class ObjectBigArrays {
    public static final Object[][] EMPTY_BIG_ARRAY = new Object[0][];
    public static final Object[][] DEFAULT_EMPTY_BIG_ARRAY = new Object[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int QUICKSORT_NO_REC = 7;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int MEDIUM = 40;

    private ObjectBigArrays() {
    }

    @Deprecated
    public static <K> K get(K[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    @Deprecated
    public static <K> void set(K[][] array, long index, K value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    @Deprecated
    public static <K> void swap(K[][] array, long first, long second) {
        K t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    @Deprecated
    public static <K> long length(K[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    @Deprecated
    public static <K> void copy(K[][] srcArray, long srcPos, K[][] destArray, long destPos, long length) {
        BigArrays.copy(srcArray, srcPos, destArray, destPos, length);
    }

    @Deprecated
    public static <K> void copyFromBig(K[][] srcArray, long srcPos, K[] destArray, int destPos, int length) {
        BigArrays.copyFromBig(srcArray, srcPos, destArray, destPos, length);
    }

    @Deprecated
    public static <K> void copyToBig(K[] srcArray, int srcPos, K[][] destArray, long destPos, long length) {
        BigArrays.copyToBig(srcArray, srcPos, destArray, destPos, length);
    }

    public static <K> K[][] newBigArray(K[][] prototype, long length) {
        return ObjectBigArrays.newBigArray(prototype.getClass().getComponentType(), length);
    }

    public static Object[][] newBigArray(Class<?> componentType, long length) {
        if (length == 0L && componentType == Object[].class) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        Object[][] base = (Object[][])Array.newInstance(componentType, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = (Object[])Array.newInstance(componentType.getComponentType(), 0x8000000);
            }
            base[baseLength - 1] = (Object[])Array.newInstance(componentType.getComponentType(), residual);
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = (Object[])Array.newInstance(componentType.getComponentType(), 0x8000000);
            }
        }
        return base;
    }

    public static Object[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        Object[][] base = new Object[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new Object[0x8000000];
            }
            base[baseLength - 1] = new Object[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new Object[0x8000000];
            }
        }
        return base;
    }

    @Deprecated
    public static <K> K[][] wrap(K[] array) {
        return BigArrays.wrap(array);
    }

    @Deprecated
    public static <K> K[][] ensureCapacity(K[][] array, long length) {
        return ObjectBigArrays.ensureCapacity(array, length, ObjectBigArrays.length(array));
    }

    @Deprecated
    public static <K> K[][] forceCapacity(K[][] array, long length, long preserve) {
        return BigArrays.forceCapacity(array, length, preserve);
    }

    @Deprecated
    public static <K> K[][] ensureCapacity(K[][] array, long length, long preserve) {
        return length > ObjectBigArrays.length(array) ? ObjectBigArrays.forceCapacity(array, length, preserve) : array;
    }

    @Deprecated
    public static <K> K[][] grow(K[][] array, long length) {
        long oldLength = ObjectBigArrays.length(array);
        return length > oldLength ? ObjectBigArrays.grow(array, length, oldLength) : array;
    }

    @Deprecated
    public static <K> K[][] grow(K[][] array, long length, long preserve) {
        long oldLength = ObjectBigArrays.length(array);
        return length > oldLength ? ObjectBigArrays.ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
    }

    @Deprecated
    public static <K> K[][] trim(K[][] array, long length) {
        return BigArrays.trim(array, length);
    }

    @Deprecated
    public static <K> K[][] setLength(K[][] array, long length) {
        return BigArrays.setLength(array, length);
    }

    @Deprecated
    public static <K> K[][] copy(K[][] array, long offset, long length) {
        return BigArrays.copy(array, offset, length);
    }

    @Deprecated
    public static <K> K[][] copy(K[][] array) {
        return BigArrays.copy(array);
    }

    @Deprecated
    public static <K> void fill(K[][] array, K value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    @Deprecated
    public static <K> void fill(K[][] array, long from, long to, K value) {
        BigArrays.fill(array, from, to, value);
    }

    @Deprecated
    public static <K> boolean equals(K[][] a1, K[][] a2) {
        return BigArrays.equals(a1, a2);
    }

    @Deprecated
    public static <K> String toString(K[][] a) {
        return BigArrays.toString(a);
    }

    @Deprecated
    public static <K> void ensureFromTo(K[][] a, long from, long to) {
        BigArrays.ensureFromTo(ObjectBigArrays.length(a), from, to);
    }

    @Deprecated
    public static <K> void ensureOffsetLength(K[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(ObjectBigArrays.length(a), offset, length);
    }

    @Deprecated
    public static <K> void ensureSameLength(K[][] a, K[][] b) {
        if (ObjectBigArrays.length(a) != ObjectBigArrays.length(b)) {
            throw new IllegalArgumentException("Array size mismatch: " + ObjectBigArrays.length(a) + " != " + ObjectBigArrays.length(b));
        }
    }

    private static ForkJoinPool getPool() {
        ForkJoinPool current = ForkJoinTask.getPool();
        return current == null ? ForkJoinPool.commonPool() : current;
    }

    private static <K> void swap(K[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            BigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static <K> long med3(K[][] x, long a, long b, long c, Comparator<K> comp) {
        int ab = comp.compare(BigArrays.get(x, a), BigArrays.get(x, b));
        int ac = comp.compare(BigArrays.get(x, a), BigArrays.get(x, c));
        int bc = comp.compare(BigArrays.get(x, b), BigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static <K> void selectionSort(K[][] a, long from, long to, Comparator<K> comp) {
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

    public static <K> void quickSort(K[][] x, long from, long to, Comparator<K> comp) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            ObjectBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = ObjectBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = ObjectBigArrays.med3(x, m - s, m, m + s, comp);
                n = ObjectBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = ObjectBigArrays.med3(x, l, m, n, comp);
        }
        K v = BigArrays.get(x, m);
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
        ObjectBigArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        ObjectBigArrays.swap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            ObjectBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            ObjectBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static <K> long med3(K[][] x, long a, long b, long c) {
        int ab = ((Comparable)BigArrays.get(x, a)).compareTo(BigArrays.get(x, b));
        int ac = ((Comparable)BigArrays.get(x, a)).compareTo(BigArrays.get(x, c));
        int bc = ((Comparable)BigArrays.get(x, b)).compareTo(BigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static <K> void selectionSort(K[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (((Comparable)BigArrays.get(a, j)).compareTo(BigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            BigArrays.swap(a, i, m);
        }
    }

    public static <K> void quickSort(K[][] x, Comparator<K> comp) {
        ObjectBigArrays.quickSort(x, 0L, BigArrays.length(x), comp);
    }

    public static <K> void quickSort(K[][] x, long from, long to) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            ObjectBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = ObjectBigArrays.med3(x, l, l + s, l + 2L * s);
                m = ObjectBigArrays.med3(x, m - s, m, m + s);
                n = ObjectBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = ObjectBigArrays.med3(x, l, m, n);
        }
        K v = BigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        while (true) {
            int comparison;
            if (b <= c && (comparison = ((Comparable)BigArrays.get(x, b)).compareTo(v)) <= 0) {
                if (comparison == 0) {
                    BigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = ((Comparable)BigArrays.get(x, c)).compareTo(v)) >= 0) {
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
        ObjectBigArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        ObjectBigArrays.swap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            ObjectBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            ObjectBigArrays.quickSort(x, n - s, n);
        }
    }

    public static <K> void quickSort(K[][] x) {
        ObjectBigArrays.quickSort(x, 0L, BigArrays.length(x));
    }

    public static <K> void parallelQuickSort(K[][] x, long from, long to) {
        ForkJoinPool pool = ObjectBigArrays.getPool();
        if (to - from < 8192L || pool.getParallelism() == 1) {
            ObjectBigArrays.quickSort(x, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSort<K>(x, from, to));
        }
    }

    public static <K> void parallelQuickSort(K[][] x) {
        ObjectBigArrays.parallelQuickSort(x, 0L, BigArrays.length(x));
    }

    public static <K> void parallelQuickSort(K[][] x, long from, long to, Comparator<K> comp) {
        ForkJoinPool pool = ObjectBigArrays.getPool();
        if (to - from < 8192L || pool.getParallelism() == 1) {
            ObjectBigArrays.quickSort(x, from, to, comp);
        } else {
            pool.invoke(new ForkJoinQuickSortComp<K>(x, from, to, comp));
        }
    }

    public static <K> void parallelQuickSort(K[][] x, Comparator<K> comp) {
        ObjectBigArrays.parallelQuickSort(x, 0L, BigArrays.length(x), comp);
    }

    public static <K> long binarySearch(K[][] a, long from, long to, K key) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            K midVal = BigArrays.get(a, mid);
            int cmp = ((Comparable)midVal).compareTo(key);
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

    public static <K> long binarySearch(K[][] a, Object key) {
        return ObjectBigArrays.binarySearch(a, 0L, BigArrays.length(a), key);
    }

    public static <K> long binarySearch(K[][] a, long from, long to, K key, Comparator<K> c) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            K midVal = BigArrays.get(a, mid);
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

    public static <K> long binarySearch(K[][] a, K key, Comparator<K> c) {
        return ObjectBigArrays.binarySearch(a, 0L, BigArrays.length(a), key, c);
    }

    public static <K> K[][] shuffle(K[][] a, long from, long to, Random random) {
        return BigArrays.shuffle(a, from, to, random);
    }

    public static <K> K[][] shuffle(K[][] a, Random random) {
        return BigArrays.shuffle(a, random);
    }

    protected static class ForkJoinQuickSort<K>
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final long from;
        private final long to;
        private final K[][] x;

        public ForkJoinQuickSort(K[][] x, long from, long to) {
            this.from = from;
            this.to = to;
            this.x = x;
        }

        @Override
        protected void compute() {
            long c;
            long a;
            Object[][] x = this.x;
            long len = this.to - this.from;
            if (len < 8192L) {
                ObjectBigArrays.quickSort(x, this.from, this.to);
                return;
            }
            long m = this.from + len / 2L;
            long l = this.from;
            long n = this.to - 1L;
            long s = len / 8L;
            l = ObjectBigArrays.med3(x, l, l + s, l + 2L * s);
            m = ObjectBigArrays.med3(x, m - s, m, m + s);
            n = ObjectBigArrays.med3(x, n - 2L * s, n - s, n);
            m = ObjectBigArrays.med3(x, l, m, n);
            Object v = BigArrays.get(x, m);
            long b = a = this.from;
            long d = c = this.to - 1L;
            while (true) {
                int comparison;
                if (b <= c && (comparison = ((Comparable)BigArrays.get(x, b)).compareTo(v)) <= 0) {
                    if (comparison == 0) {
                        BigArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = ((Comparable)BigArrays.get(x, c)).compareTo(v)) >= 0) {
                    if (comparison == 0) {
                        BigArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                BigArrays.swap(x, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            ObjectBigArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1L);
            ObjectBigArrays.swap(x, b, this.to - s, s);
            s = b - a;
            long t = d - c;
            if (s > 1L && t > 1L) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort<Object>(x, this.from, this.from + s), new ForkJoinQuickSort<Object>(x, this.to - t, this.to));
            } else if (s > 1L) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort<Object>(x, this.from, this.from + s));
            } else {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort<Object>(x, this.to - t, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSortComp<K>
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final long from;
        private final long to;
        private final K[][] x;
        private final Comparator<K> comp;

        public ForkJoinQuickSortComp(K[][] x, long from, long to, Comparator<K> comp) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.comp = comp;
        }

        @Override
        protected void compute() {
            long c;
            long a;
            Object[][] x = this.x;
            long len = this.to - this.from;
            if (len < 8192L) {
                ObjectBigArrays.quickSort(x, this.from, this.to, this.comp);
                return;
            }
            long m = this.from + len / 2L;
            long l = this.from;
            long n = this.to - 1L;
            long s = len / 8L;
            l = ObjectBigArrays.med3(x, l, l + s, l + 2L * s, this.comp);
            m = ObjectBigArrays.med3(x, m - s, m, m + s, this.comp);
            n = ObjectBigArrays.med3(x, n - 2L * s, n - s, n, this.comp);
            m = ObjectBigArrays.med3(x, l, m, n, this.comp);
            Object v = BigArrays.get(x, m);
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
            ObjectBigArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1L);
            ObjectBigArrays.swap(x, b, this.to - s, s);
            s = b - a;
            long t = d - c;
            if (s > 1L && t > 1L) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp<Object>(x, this.from, this.from + s, this.comp), new ForkJoinQuickSortComp<Object>(x, this.to - t, this.to, this.comp));
            } else if (s > 1L) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp<Object>(x, this.from, this.from + s, this.comp));
            } else {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp<Object>(x, this.to - t, this.to, this.comp));
            }
        }
    }

    private static final class BigArrayHashStrategy<K>
    implements Hash.Strategy<K[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(K[][] o) {
            return Arrays.deepHashCode(o);
        }

        @Override
        public boolean equals(K[][] a, K[][] b) {
            return ObjectBigArrays.equals(a, b);
        }
    }
}

