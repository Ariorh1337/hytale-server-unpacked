/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.ints.IntBigListIterator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;

public interface IntBigList
extends BigList<Integer>,
IntCollection,
Comparable<BigList<? extends Integer>> {
    @Override
    public IntBigListIterator iterator();

    public IntBigListIterator listIterator();

    public IntBigListIterator listIterator(long var1);

    @Override
    default public IntSpliterator spliterator() {
        return IntSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
    }

    public IntBigList subList(long var1, long var3);

    public void getElements(long var1, int[][] var3, long var4, long var6);

    default public void getElements(long from, int[] a, int offset, int length) {
        this.getElements(from, new int[][]{a}, (long)offset, (long)length);
    }

    public void removeElements(long var1, long var3);

    public void addElements(long var1, int[][] var3);

    public void addElements(long var1, int[][] var3, long var4, long var6);

    default public void setElements(int[][] a) {
        this.setElements(0L, a);
    }

    default public void setElements(long index, int[][] a) {
        this.setElements(index, a, 0L, BigArrays.length(a));
    }

    default public void setElements(long index, int[][] a, long offset, long length) {
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
        IntBigListIterator iter = this.listIterator(index);
        long i = 0L;
        while (i < length) {
            iter.nextInt();
            iter.set(BigArrays.get(a, offset + i++));
        }
    }

    @Override
    public void add(long var1, int var3);

    public boolean addAll(long var1, IntCollection var3);

    public int getInt(long var1);

    public int removeInt(long var1);

    @Override
    public int set(long var1, int var3);

    public long indexOf(int var1);

    public long lastIndexOf(int var1);

    @Override
    @Deprecated
    public void add(long var1, Integer var3);

    @Override
    @Deprecated
    public Integer get(long var1);

    @Override
    @Deprecated
    public long indexOf(Object var1);

    @Override
    @Deprecated
    public long lastIndexOf(Object var1);

    @Override
    @Deprecated
    public Integer remove(long var1);

    @Override
    @Deprecated
    public Integer set(long var1, Integer var3);

    default public boolean addAll(long index, IntBigList l) {
        return this.addAll(index, (IntCollection)l);
    }

    default public boolean addAll(IntBigList l) {
        return this.addAll(this.size64(), l);
    }

    default public boolean addAll(long index, IntList l) {
        return this.addAll(index, (IntCollection)l);
    }

    default public boolean addAll(IntList l) {
        return this.addAll(this.size64(), l);
    }
}

