/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.util;

import com.hypixel.hytale.function.predicate.UnaryBiPredicate;
import com.hypixel.hytale.math.util.MathUtil;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArrayUtil {
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public static final long[] EMPTY_LONG_ARRAY = new long[0];
    public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    public static final Integer[] EMPTY_INTEGER_ARRAY = new Integer[0];
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final BitSet[] EMPTY_BITSET_ARRAY = new BitSet[0];
    public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final Supplier[] EMPTY_SUPPLIER_ARRAY = new Supplier[0];
    private static final Map.Entry[] EMPTY_ENTRY_ARRAY = new Map.Entry[0];

    @Nonnull
    public static <T> T[] emptyArray() {
        return EMPTY_OBJECT_ARRAY;
    }

    @Nonnull
    public static <T> Supplier<T>[] emptySupplierArray() {
        return EMPTY_SUPPLIER_ARRAY;
    }

    @Nonnull
    public static <K, V> Map.Entry<K, V>[] emptyEntryArray() {
        return EMPTY_ENTRY_ARRAY;
    }

    public static int grow(int oldSize) {
        return (int)MathUtil.clamp((long)oldSize + (long)(oldSize >> 1), 2L, 0x7FFFFFF7L);
    }

    public static <StartType, EndType> EndType[] copyAndMutate(@Nullable StartType[] array, @Nonnull Function<StartType, EndType> adapter, @Nonnull IntFunction<EndType[]> arrayProvider) {
        if (array == null) {
            return null;
        }
        EndType[] endArray = arrayProvider.apply(array.length);
        for (int i = 0; i < endArray.length; ++i) {
            endArray[i] = adapter.apply(array[i]);
        }
        return endArray;
    }

    @Nullable
    public static <T> T[] combine(@Nullable T[] a1, @Nullable T[] a2) {
        if (a1 == null || a1.length == 0) {
            return a2;
        }
        if (a2 == null || a2.length == 0) {
            return a1;
        }
        T[] newArray = Arrays.copyOf(a1, a1.length + a2.length);
        System.arraycopy(a2, 0, newArray, a1.length, a2.length);
        return newArray;
    }

    @Nonnull
    public static <T> T[] append(@Nullable T[] arr, @Nonnull T t) {
        if (arr == null) {
            Object[] newArray = (Object[])Array.newInstance(t.getClass(), 1);
            newArray[0] = t;
            return newArray;
        }
        T[] newArray = Arrays.copyOf(arr, arr.length + 1);
        newArray[arr.length] = t;
        return newArray;
    }

    @Nonnull
    public static <T> T[] remove(@Nonnull T[] arr, int index) {
        int newLength = arr.length - 1;
        Object[] newArray = (Object[])Array.newInstance(arr.getClass().getComponentType(), newLength);
        System.arraycopy(arr, 0, newArray, 0, index);
        if (index < newLength) {
            System.arraycopy(arr, index + 1, newArray, index, newLength - index);
        }
        return newArray;
    }

    public static boolean startsWith(@Nonnull byte[] array, @Nonnull byte[] start) {
        if (start.length > array.length) {
            return false;
        }
        for (int i = 0; i < start.length; ++i) {
            if (array[i] == start[i]) continue;
            return false;
        }
        return true;
    }

    public static <T> boolean equals(@Nullable T[] a, @Nullable T[] a2, @Nonnull UnaryBiPredicate<T> predicate) {
        if (a == a2) {
            return true;
        }
        if (a == null || a2 == null) {
            return false;
        }
        int length = a.length;
        if (a2.length != length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            T o1 = a[i];
            T o2 = a2[i];
            if (o1 != null ? predicate.test(o1, o2) : o2 == null) continue;
            return false;
        }
        return true;
    }

    @Nonnull
    public static <T> T[][] split(@Nonnull T[] data, int size) {
        Class<?> aClass = data.getClass();
        Object[][] ret = (Object[][])Array.newInstance(aClass.getComponentType(), MathUtil.ceil((double)data.length / (double)size), 0);
        int start = 0;
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = Arrays.copyOfRange(data, start, Math.min(start + size, data.length));
            start += size;
        }
        return ret;
    }

    public static byte[][] split(@Nonnull byte[] data, int size) {
        byte[][] ret = new byte[MathUtil.ceil((double)data.length / (double)size)][];
        int start = 0;
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = Arrays.copyOfRange(data, start, Math.min(start + size, data.length));
            start += size;
        }
        return ret;
    }

    public static void shuffleArray(@Nonnull int[] ar, int from, int to, @Nonnull Random rnd) {
        Objects.checkFromToIndex(from, to, ar.length);
        for (int i = to - 1; i > from; --i) {
            int index = rnd.nextInt(i + 1 - from) + from;
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public static void shuffleArray(@Nonnull byte[] ar, int from, int to, @Nonnull Random rnd) {
        Objects.checkFromToIndex(from, to, ar.length);
        for (int i = to - 1; i > from; --i) {
            int index = rnd.nextInt(i + 1 - from) + from;
            byte a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public static <T> boolean contains(@Nonnull T[] array, @Nullable T obj) {
        return ArrayUtil.indexOf(array, obj) >= 0;
    }

    public static <T> boolean contains(@Nonnull T[] array, @Nullable T obj, int start, int end) {
        return ArrayUtil.indexOf(array, obj, start, end) >= 0;
    }

    public static <T> int indexOf(@Nonnull T[] array, @Nullable T obj) {
        return ArrayUtil.indexOf(array, obj, 0, array.length);
    }

    public static <T> int indexOf(@Nonnull T[] array, @Nullable T obj, int start, int end) {
        if (obj == null) {
            for (int i = start; i < end; ++i) {
                if (array[i] != null) continue;
                return i;
            }
        } else {
            for (int i = start; i < end; ++i) {
                if (!obj.equals(array[i])) continue;
                return i;
            }
        }
        return -1;
    }
}

