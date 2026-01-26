/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanComparator;
import it.unimi.dsi.fastutil.booleans.BooleanComparators;
import it.unimi.dsi.fastutil.booleans.BooleanImmutableList;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanLists;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import it.unimi.dsi.fastutil.booleans.BooleanUnaryOperator;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.UnaryOperator;

public interface BooleanList
extends List<Boolean>,
Comparable<List<? extends Boolean>>,
BooleanCollection {
    @Override
    public BooleanListIterator iterator();

    @Override
    default public BooleanSpliterator spliterator() {
        if (this instanceof RandomAccess) {
            return new AbstractBooleanList.IndexBasedSpliterator(this, 0);
        }
        return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 16720);
    }

    public BooleanListIterator listIterator();

    public BooleanListIterator listIterator(int var1);

    public BooleanList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, boolean[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, boolean[] var2);

    public void addElements(int var1, boolean[] var2, int var3, int var4);

    default public void setElements(boolean[] a) {
        this.setElements(0, a);
    }

    default public void setElements(int index, boolean[] a) {
        this.setElements(index, a, 0, a.length);
    }

    default public void setElements(int index, boolean[] a, int offset, int length) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
        }
        BooleanArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
        }
        BooleanListIterator iter = this.listIterator(index);
        int i = 0;
        while (i < length) {
            iter.nextBoolean();
            iter.set(a[offset + i++]);
        }
    }

    @Override
    public boolean add(boolean var1);

    @Override
    public void add(int var1, boolean var2);

    @Override
    @Deprecated
    default public void add(int index, Boolean key) {
        this.add(index, (boolean)key);
    }

    public boolean addAll(int var1, BooleanCollection var2);

    @Override
    public boolean set(int var1, boolean var2);

    default public void replaceAll(BooleanUnaryOperator operator) {
        BooleanListIterator iter = this.listIterator();
        while (iter.hasNext()) {
            iter.set(operator.apply(iter.nextBoolean()));
        }
    }

    @Override
    @Deprecated
    default public void replaceAll(UnaryOperator<Boolean> operator) {
        Objects.requireNonNull(operator);
        this.replaceAll(operator instanceof BooleanUnaryOperator ? (BooleanUnaryOperator)operator : operator::apply);
    }

    public boolean getBoolean(int var1);

    public int indexOf(boolean var1);

    public int lastIndexOf(boolean var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return BooleanCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Boolean get(int index) {
        return this.getBoolean(index);
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf((Boolean)o);
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf((Boolean)o);
    }

    @Override
    @Deprecated
    default public boolean add(Boolean k) {
        return this.add((boolean)k);
    }

    public boolean removeBoolean(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return BooleanCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Boolean remove(int index) {
        return this.removeBoolean(index);
    }

    @Override
    @Deprecated
    default public Boolean set(int index, Boolean k) {
        return this.set(index, (boolean)k);
    }

    default public boolean addAll(int index, BooleanList l) {
        return this.addAll(index, (BooleanCollection)l);
    }

    default public boolean addAll(BooleanList l) {
        return this.addAll(this.size(), l);
    }

    public static BooleanList of() {
        return BooleanImmutableList.of();
    }

    public static BooleanList of(boolean e) {
        return BooleanLists.singleton(e);
    }

    public static BooleanList of(boolean e0, boolean e1) {
        return BooleanImmutableList.of(new boolean[]{e0, e1});
    }

    public static BooleanList of(boolean e0, boolean e1, boolean e2) {
        return BooleanImmutableList.of(new boolean[]{e0, e1, e2});
    }

    public static BooleanList of(boolean ... a) {
        switch (a.length) {
            case 0: {
                return BooleanList.of();
            }
            case 1: {
                return BooleanList.of(a[0]);
            }
        }
        return BooleanImmutableList.of(a);
    }

    @Override
    @Deprecated
    default public void sort(Comparator<? super Boolean> comparator) {
        this.sort(BooleanComparators.asBooleanComparator(comparator));
    }

    default public void sort(BooleanComparator comparator) {
        if (comparator == null) {
            this.unstableSort(comparator);
        } else {
            boolean[] elements = this.toBooleanArray();
            BooleanArrays.stableSort(elements, comparator);
            this.setElements(elements);
        }
    }

    @Deprecated
    default public void unstableSort(Comparator<? super Boolean> comparator) {
        this.unstableSort(BooleanComparators.asBooleanComparator(comparator));
    }

    default public void unstableSort(BooleanComparator comparator) {
        boolean[] elements = this.toBooleanArray();
        if (comparator == null) {
            BooleanArrays.unstableSort(elements);
        } else {
            BooleanArrays.unstableSort(elements, comparator);
        }
        this.setElements(elements);
    }
}

