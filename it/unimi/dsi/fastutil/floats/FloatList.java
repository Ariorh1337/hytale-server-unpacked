/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.floats.AbstractFloatList;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatComparators;
import it.unimi.dsi.fastutil.floats.FloatImmutableList;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.floats.FloatLists;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

public interface FloatList
extends List<Float>,
Comparable<List<? extends Float>>,
FloatCollection {
    @Override
    public FloatListIterator iterator();

    @Override
    default public FloatSpliterator spliterator() {
        if (this instanceof RandomAccess) {
            return new AbstractFloatList.IndexBasedSpliterator(this, 0);
        }
        return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 16720);
    }

    public FloatListIterator listIterator();

    public FloatListIterator listIterator(int var1);

    public FloatList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, float[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, float[] var2);

    public void addElements(int var1, float[] var2, int var3, int var4);

    default public void setElements(float[] a) {
        this.setElements(0, a);
    }

    default public void setElements(int index, float[] a) {
        this.setElements(index, a, 0, a.length);
    }

    default public void setElements(int index, float[] a, int offset, int length) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
        }
        FloatArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
        }
        FloatListIterator iter = this.listIterator(index);
        int i = 0;
        while (i < length) {
            iter.nextFloat();
            iter.set(a[offset + i++]);
        }
    }

    @Override
    public boolean add(float var1);

    @Override
    public void add(int var1, float var2);

    @Override
    @Deprecated
    default public void add(int index, Float key) {
        this.add(index, key.floatValue());
    }

    public boolean addAll(int var1, FloatCollection var2);

    @Override
    public float set(int var1, float var2);

    default public void replaceAll(FloatUnaryOperator operator) {
        FloatListIterator iter = this.listIterator();
        while (iter.hasNext()) {
            iter.set(operator.apply(iter.nextFloat()));
        }
    }

    default public void replaceAll(DoubleUnaryOperator operator) {
        this.replaceAll(operator instanceof FloatUnaryOperator ? (FloatUnaryOperator)operator : x -> SafeMath.safeDoubleToFloat(operator.applyAsDouble(x)));
    }

    @Override
    @Deprecated
    default public void replaceAll(UnaryOperator<Float> operator) {
        Objects.requireNonNull(operator);
        this.replaceAll(operator instanceof FloatUnaryOperator ? (FloatUnaryOperator)operator : operator::apply);
    }

    public float getFloat(int var1);

    public int indexOf(float var1);

    public int lastIndexOf(float var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return FloatCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Float get(int index) {
        return Float.valueOf(this.getFloat(index));
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf(((Float)o).floatValue());
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf(((Float)o).floatValue());
    }

    @Override
    @Deprecated
    default public boolean add(Float k) {
        return this.add(k.floatValue());
    }

    public float removeFloat(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return FloatCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Float remove(int index) {
        return Float.valueOf(this.removeFloat(index));
    }

    @Override
    @Deprecated
    default public Float set(int index, Float k) {
        return Float.valueOf(this.set(index, k.floatValue()));
    }

    default public boolean addAll(int index, FloatList l) {
        return this.addAll(index, (FloatCollection)l);
    }

    default public boolean addAll(FloatList l) {
        return this.addAll(this.size(), l);
    }

    public static FloatList of() {
        return FloatImmutableList.of();
    }

    public static FloatList of(float e) {
        return FloatLists.singleton(e);
    }

    public static FloatList of(float e0, float e1) {
        return FloatImmutableList.of(new float[]{e0, e1});
    }

    public static FloatList of(float e0, float e1, float e2) {
        return FloatImmutableList.of(new float[]{e0, e1, e2});
    }

    public static FloatList of(float ... a) {
        switch (a.length) {
            case 0: {
                return FloatList.of();
            }
            case 1: {
                return FloatList.of(a[0]);
            }
        }
        return FloatImmutableList.of(a);
    }

    @Override
    @Deprecated
    default public void sort(Comparator<? super Float> comparator) {
        this.sort(FloatComparators.asFloatComparator(comparator));
    }

    default public void sort(FloatComparator comparator) {
        float[] elements = this.toFloatArray();
        if (comparator == null) {
            FloatArrays.stableSort(elements);
        } else {
            FloatArrays.stableSort(elements, comparator);
        }
        this.setElements(elements);
    }

    @Deprecated
    default public void unstableSort(Comparator<? super Float> comparator) {
        this.unstableSort(FloatComparators.asFloatComparator(comparator));
    }

    default public void unstableSort(FloatComparator comparator) {
        float[] elements = this.toFloatArray();
        if (comparator == null) {
            FloatArrays.unstableSort(elements);
        } else {
            FloatArrays.unstableSort(elements, comparator);
        }
        this.setElements(elements);
    }
}

