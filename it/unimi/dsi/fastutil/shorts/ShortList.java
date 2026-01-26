/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.shorts.AbstractShortList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortComparators;
import it.unimi.dsi.fastutil.shorts.ShortImmutableList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import it.unimi.dsi.fastutil.shorts.ShortLists;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import it.unimi.dsi.fastutil.shorts.ShortUnaryOperator;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

public interface ShortList
extends List<Short>,
Comparable<List<? extends Short>>,
ShortCollection {
    @Override
    public ShortListIterator iterator();

    @Override
    default public ShortSpliterator spliterator() {
        if (this instanceof RandomAccess) {
            return new AbstractShortList.IndexBasedSpliterator(this, 0);
        }
        return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 16720);
    }

    public ShortListIterator listIterator();

    public ShortListIterator listIterator(int var1);

    public ShortList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, short[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, short[] var2);

    public void addElements(int var1, short[] var2, int var3, int var4);

    default public void setElements(short[] a) {
        this.setElements(0, a);
    }

    default public void setElements(int index, short[] a) {
        this.setElements(index, a, 0, a.length);
    }

    default public void setElements(int index, short[] a, int offset, int length) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
        }
        ShortArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
        }
        ShortListIterator iter = this.listIterator(index);
        int i = 0;
        while (i < length) {
            iter.nextShort();
            iter.set(a[offset + i++]);
        }
    }

    @Override
    public boolean add(short var1);

    @Override
    public void add(int var1, short var2);

    @Override
    @Deprecated
    default public void add(int index, Short key) {
        this.add(index, (short)key);
    }

    public boolean addAll(int var1, ShortCollection var2);

    @Override
    public short set(int var1, short var2);

    default public void replaceAll(ShortUnaryOperator operator) {
        ShortListIterator iter = this.listIterator();
        while (iter.hasNext()) {
            iter.set(operator.apply(iter.nextShort()));
        }
    }

    default public void replaceAll(IntUnaryOperator operator) {
        this.replaceAll(operator instanceof ShortUnaryOperator ? (ShortUnaryOperator)operator : x -> SafeMath.safeIntToShort(operator.applyAsInt(x)));
    }

    @Override
    @Deprecated
    default public void replaceAll(UnaryOperator<Short> operator) {
        Objects.requireNonNull(operator);
        this.replaceAll(operator instanceof ShortUnaryOperator ? (ShortUnaryOperator)operator : operator::apply);
    }

    public short getShort(int var1);

    public int indexOf(short var1);

    public int lastIndexOf(short var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return ShortCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Short get(int index) {
        return this.getShort(index);
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf((Short)o);
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf((Short)o);
    }

    @Override
    @Deprecated
    default public boolean add(Short k) {
        return this.add((short)k);
    }

    public short removeShort(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return ShortCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Short remove(int index) {
        return this.removeShort(index);
    }

    @Override
    @Deprecated
    default public Short set(int index, Short k) {
        return this.set(index, (short)k);
    }

    default public boolean addAll(int index, ShortList l) {
        return this.addAll(index, (ShortCollection)l);
    }

    default public boolean addAll(ShortList l) {
        return this.addAll(this.size(), l);
    }

    public static ShortList of() {
        return ShortImmutableList.of();
    }

    public static ShortList of(short e) {
        return ShortLists.singleton(e);
    }

    public static ShortList of(short e0, short e1) {
        return ShortImmutableList.of(new short[]{e0, e1});
    }

    public static ShortList of(short e0, short e1, short e2) {
        return ShortImmutableList.of(new short[]{e0, e1, e2});
    }

    public static ShortList of(short ... a) {
        switch (a.length) {
            case 0: {
                return ShortList.of();
            }
            case 1: {
                return ShortList.of(a[0]);
            }
        }
        return ShortImmutableList.of(a);
    }

    @Override
    @Deprecated
    default public void sort(Comparator<? super Short> comparator) {
        this.sort(ShortComparators.asShortComparator(comparator));
    }

    default public void sort(ShortComparator comparator) {
        if (comparator == null) {
            this.unstableSort(comparator);
        } else {
            short[] elements = this.toShortArray();
            ShortArrays.stableSort(elements, comparator);
            this.setElements(elements);
        }
    }

    @Deprecated
    default public void unstableSort(Comparator<? super Short> comparator) {
        this.unstableSort(ShortComparators.asShortComparator(comparator));
    }

    default public void unstableSort(ShortComparator comparator) {
        short[] elements = this.toShortArray();
        if (comparator == null) {
            ShortArrays.unstableSort(elements);
        } else {
            ShortArrays.unstableSort(elements, comparator);
        }
        this.setElements(elements);
    }
}

