/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleComparators;
import it.unimi.dsi.fastutil.doubles.DoubleImmutableList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleLists;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import it.unimi.dsi.fastutil.doubles.DoubleUnaryOperator;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.UnaryOperator;

public interface DoubleList
extends List<Double>,
Comparable<List<? extends Double>>,
DoubleCollection {
    @Override
    public DoubleListIterator iterator();

    @Override
    default public DoubleSpliterator spliterator() {
        if (this instanceof RandomAccess) {
            return new AbstractDoubleList.IndexBasedSpliterator(this, 0);
        }
        return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 16720);
    }

    public DoubleListIterator listIterator();

    public DoubleListIterator listIterator(int var1);

    public DoubleList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, double[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, double[] var2);

    public void addElements(int var1, double[] var2, int var3, int var4);

    default public void setElements(double[] a) {
        this.setElements(0, a);
    }

    default public void setElements(int index, double[] a) {
        this.setElements(index, a, 0, a.length);
    }

    default public void setElements(int index, double[] a, int offset, int length) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
        }
        DoubleArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
        }
        DoubleListIterator iter = this.listIterator(index);
        int i = 0;
        while (i < length) {
            iter.nextDouble();
            iter.set(a[offset + i++]);
        }
    }

    @Override
    public boolean add(double var1);

    @Override
    public void add(int var1, double var2);

    @Override
    @Deprecated
    default public void add(int index, Double key) {
        this.add(index, (double)key);
    }

    public boolean addAll(int var1, DoubleCollection var2);

    @Override
    public double set(int var1, double var2);

    default public void replaceAll(java.util.function.DoubleUnaryOperator operator) {
        DoubleListIterator iter = this.listIterator();
        while (iter.hasNext()) {
            iter.set(operator.applyAsDouble(iter.nextDouble()));
        }
    }

    default public void replaceAll(DoubleUnaryOperator operator) {
        this.replaceAll((java.util.function.DoubleUnaryOperator)operator);
    }

    @Override
    @Deprecated
    default public void replaceAll(UnaryOperator<Double> operator) {
        Objects.requireNonNull(operator);
        this.replaceAll(operator instanceof java.util.function.DoubleUnaryOperator ? (java.util.function.DoubleUnaryOperator)((Object)operator) : operator::apply);
    }

    public double getDouble(int var1);

    public int indexOf(double var1);

    public int lastIndexOf(double var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return DoubleCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Double get(int index) {
        return this.getDouble(index);
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf((Double)o);
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf((Double)o);
    }

    @Override
    @Deprecated
    default public boolean add(Double k) {
        return this.add((double)k);
    }

    public double removeDouble(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return DoubleCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Double remove(int index) {
        return this.removeDouble(index);
    }

    @Override
    @Deprecated
    default public Double set(int index, Double k) {
        return this.set(index, (double)k);
    }

    default public boolean addAll(int index, DoubleList l) {
        return this.addAll(index, (DoubleCollection)l);
    }

    default public boolean addAll(DoubleList l) {
        return this.addAll(this.size(), l);
    }

    public static DoubleList of() {
        return DoubleImmutableList.of();
    }

    public static DoubleList of(double e) {
        return DoubleLists.singleton(e);
    }

    public static DoubleList of(double e0, double e1) {
        return DoubleImmutableList.of(new double[]{e0, e1});
    }

    public static DoubleList of(double e0, double e1, double e2) {
        return DoubleImmutableList.of(new double[]{e0, e1, e2});
    }

    public static DoubleList of(double ... a) {
        switch (a.length) {
            case 0: {
                return DoubleList.of();
            }
            case 1: {
                return DoubleList.of(a[0]);
            }
        }
        return DoubleImmutableList.of(a);
    }

    @Override
    @Deprecated
    default public void sort(Comparator<? super Double> comparator) {
        this.sort(DoubleComparators.asDoubleComparator(comparator));
    }

    default public void sort(DoubleComparator comparator) {
        double[] elements = this.toDoubleArray();
        if (comparator == null) {
            DoubleArrays.stableSort(elements);
        } else {
            DoubleArrays.stableSort(elements, comparator);
        }
        this.setElements(elements);
    }

    @Deprecated
    default public void unstableSort(Comparator<? super Double> comparator) {
        this.unstableSort(DoubleComparators.asDoubleComparator(comparator));
    }

    default public void unstableSort(DoubleComparator comparator) {
        double[] elements = this.toDoubleArray();
        if (comparator == null) {
            DoubleArrays.unstableSort(elements);
        } else {
            DoubleArrays.unstableSort(elements, comparator);
        }
        this.setElements(elements);
    }
}

