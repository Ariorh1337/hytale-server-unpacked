/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.floats.FloatBigListIterator;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;

public interface FloatBigList
extends BigList<Float>,
FloatCollection,
Comparable<BigList<? extends Float>> {
    @Override
    public FloatBigListIterator iterator();

    public FloatBigListIterator listIterator();

    public FloatBigListIterator listIterator(long var1);

    @Override
    default public FloatSpliterator spliterator() {
        return FloatSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
    }

    public FloatBigList subList(long var1, long var3);

    public void getElements(long var1, float[][] var3, long var4, long var6);

    default public void getElements(long from, float[] a, int offset, int length) {
        this.getElements(from, new float[][]{a}, (long)offset, (long)length);
    }

    public void removeElements(long var1, long var3);

    public void addElements(long var1, float[][] var3);

    public void addElements(long var1, float[][] var3, long var4, long var6);

    default public void setElements(float[][] a) {
        this.setElements(0L, a);
    }

    default public void setElements(long index, float[][] a) {
        this.setElements(index, a, 0L, BigArrays.length(a));
    }

    default public void setElements(long index, float[][] a, long offset, long length) {
        if (index < 0L) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size64()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
        }
        BigArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
        }
        FloatBigListIterator iter = this.listIterator(index);
        long i = 0L;
        while (i < length) {
            iter.nextFloat();
            iter.set(BigArrays.get(a, offset + i++));
        }
    }

    @Override
    public void add(long var1, float var3);

    public boolean addAll(long var1, FloatCollection var3);

    public float getFloat(long var1);

    public float removeFloat(long var1);

    @Override
    public float set(long var1, float var3);

    public long indexOf(float var1);

    public long lastIndexOf(float var1);

    @Override
    @Deprecated
    public void add(long var1, Float var3);

    @Override
    @Deprecated
    public Float get(long var1);

    @Override
    @Deprecated
    public long indexOf(Object var1);

    @Override
    @Deprecated
    public long lastIndexOf(Object var1);

    @Override
    @Deprecated
    public Float remove(long var1);

    @Override
    @Deprecated
    public Float set(long var1, Float var3);

    default public boolean addAll(long index, FloatBigList l) {
        return this.addAll(index, (FloatCollection)l);
    }

    default public boolean addAll(FloatBigList l) {
        return this.addAll(this.size64(), l);
    }

    default public boolean addAll(long index, FloatList l) {
        return this.addAll(index, (FloatCollection)l);
    }

    default public boolean addAll(FloatList l) {
        return this.addAll(this.size64(), l);
    }
}

