/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.longs.AbstractLongList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongComparators;
import it.unimi.dsi.fastutil.longs.LongImmutableList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongLists;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import it.unimi.dsi.fastutil.longs.LongUnaryOperator;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.UnaryOperator;

public interface LongList
extends List<Long>,
Comparable<List<? extends Long>>,
LongCollection {
    @Override
    public LongListIterator iterator();

    @Override
    default public LongSpliterator spliterator() {
        if (this instanceof RandomAccess) {
            return new AbstractLongList.IndexBasedSpliterator(this, 0);
        }
        return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 16720);
    }

    public LongListIterator listIterator();

    public LongListIterator listIterator(int var1);

    public LongList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, long[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, long[] var2);

    public void addElements(int var1, long[] var2, int var3, int var4);

    default public void setElements(long[] a) {
        this.setElements(0, a);
    }

    default public void setElements(int index, long[] a) {
        this.setElements(index, a, 0, a.length);
    }

    default public void setElements(int index, long[] a, int offset, int length) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
        }
        LongArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
        }
        LongListIterator iter = this.listIterator(index);
        int i = 0;
        while (i < length) {
            iter.nextLong();
            iter.set(a[offset + i++]);
        }
    }

    @Override
    public boolean add(long var1);

    @Override
    public void add(int var1, long var2);

    @Override
    @Deprecated
    default public void add(int index, Long key) {
        this.add(index, (long)key);
    }

    public boolean addAll(int var1, LongCollection var2);

    @Override
    public long set(int var1, long var2);

    default public void replaceAll(java.util.function.LongUnaryOperator operator) {
        LongListIterator iter = this.listIterator();
        while (iter.hasNext()) {
            iter.set(operator.applyAsLong(iter.nextLong()));
        }
    }

    default public void replaceAll(LongUnaryOperator operator) {
        this.replaceAll((java.util.function.LongUnaryOperator)operator);
    }

    @Override
    @Deprecated
    default public void replaceAll(UnaryOperator<Long> operator) {
        Objects.requireNonNull(operator);
        this.replaceAll(operator instanceof java.util.function.LongUnaryOperator ? (java.util.function.LongUnaryOperator)((Object)operator) : operator::apply);
    }

    public long getLong(int var1);

    public int indexOf(long var1);

    public int lastIndexOf(long var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return LongCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Long get(int index) {
        return this.getLong(index);
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf((Long)o);
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf((Long)o);
    }

    @Override
    @Deprecated
    default public boolean add(Long k) {
        return this.add((long)k);
    }

    public long removeLong(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return LongCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Long remove(int index) {
        return this.removeLong(index);
    }

    @Override
    @Deprecated
    default public Long set(int index, Long k) {
        return this.set(index, (long)k);
    }

    default public boolean addAll(int index, LongList l) {
        return this.addAll(index, (LongCollection)l);
    }

    default public boolean addAll(LongList l) {
        return this.addAll(this.size(), l);
    }

    public static LongList of() {
        return LongImmutableList.of();
    }

    public static LongList of(long e) {
        return LongLists.singleton(e);
    }

    public static LongList of(long e0, long e1) {
        return LongImmutableList.of(new long[]{e0, e1});
    }

    public static LongList of(long e0, long e1, long e2) {
        return LongImmutableList.of(new long[]{e0, e1, e2});
    }

    public static LongList of(long ... a) {
        switch (a.length) {
            case 0: {
                return LongList.of();
            }
            case 1: {
                return LongList.of(a[0]);
            }
        }
        return LongImmutableList.of(a);
    }

    @Override
    @Deprecated
    default public void sort(Comparator<? super Long> comparator) {
        this.sort(LongComparators.asLongComparator(comparator));
    }

    default public void sort(LongComparator comparator) {
        if (comparator == null) {
            this.unstableSort(comparator);
        } else {
            long[] elements = this.toLongArray();
            LongArrays.stableSort(elements, comparator);
            this.setElements(elements);
        }
    }

    @Deprecated
    default public void unstableSort(Comparator<? super Long> comparator) {
        this.unstableSort(LongComparators.asLongComparator(comparator));
    }

    default public void unstableSort(LongComparator comparator) {
        long[] elements = this.toLongArray();
        if (comparator == null) {
            LongArrays.unstableSort(elements);
        } else {
            LongArrays.unstableSort(elements, comparator);
        }
        this.setElements(elements);
    }
}

