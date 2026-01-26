/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.doubles.DoubleBigListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;

public interface DoubleBigList
extends BigList<Double>,
DoubleCollection,
Comparable<BigList<? extends Double>> {
    @Override
    public DoubleBigListIterator iterator();

    public DoubleBigListIterator listIterator();

    public DoubleBigListIterator listIterator(long var1);

    @Override
    default public DoubleSpliterator spliterator() {
        return DoubleSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
    }

    public DoubleBigList subList(long var1, long var3);

    public void getElements(long var1, double[][] var3, long var4, long var6);

    default public void getElements(long from, double[] a, int offset, int length) {
        this.getElements(from, new double[][]{a}, (long)offset, (long)length);
    }

    public void removeElements(long var1, long var3);

    public void addElements(long var1, double[][] var3);

    public void addElements(long var1, double[][] var3, long var4, long var6);

    default public void setElements(double[][] a) {
        this.setElements(0L, a);
    }

    default public void setElements(long index, double[][] a) {
        this.setElements(index, a, 0L, BigArrays.length(a));
    }

    default public void setElements(long index, double[][] a, long offset, long length) {
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
        DoubleBigListIterator iter = this.listIterator(index);
        long i = 0L;
        while (i < length) {
            iter.nextDouble();
            iter.set(BigArrays.get(a, offset + i++));
        }
    }

    @Override
    public void add(long var1, double var3);

    public boolean addAll(long var1, DoubleCollection var3);

    public double getDouble(long var1);

    public double removeDouble(long var1);

    @Override
    public double set(long var1, double var3);

    public long indexOf(double var1);

    public long lastIndexOf(double var1);

    @Override
    @Deprecated
    public void add(long var1, Double var3);

    @Override
    @Deprecated
    public Double get(long var1);

    @Override
    @Deprecated
    public long indexOf(Object var1);

    @Override
    @Deprecated
    public long lastIndexOf(Object var1);

    @Override
    @Deprecated
    public Double remove(long var1);

    @Override
    @Deprecated
    public Double set(long var1, Double var3);

    default public boolean addAll(long index, DoubleBigList l) {
        return this.addAll(index, (DoubleCollection)l);
    }

    default public boolean addAll(DoubleBigList l) {
        return this.addAll(this.size64(), l);
    }

    default public boolean addAll(long index, DoubleList l) {
        return this.addAll(index, (DoubleCollection)l);
    }

    default public boolean addAll(DoubleList l) {
        return this.addAll(this.size64(), l);
    }
}

