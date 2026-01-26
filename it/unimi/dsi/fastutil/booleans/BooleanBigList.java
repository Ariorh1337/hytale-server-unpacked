/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.booleans.BooleanBigListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;

public interface BooleanBigList
extends BigList<Boolean>,
BooleanCollection,
Comparable<BigList<? extends Boolean>> {
    @Override
    public BooleanBigListIterator iterator();

    public BooleanBigListIterator listIterator();

    public BooleanBigListIterator listIterator(long var1);

    @Override
    default public BooleanSpliterator spliterator() {
        return BooleanSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
    }

    public BooleanBigList subList(long var1, long var3);

    public void getElements(long var1, boolean[][] var3, long var4, long var6);

    default public void getElements(long from, boolean[] a, int offset, int length) {
        this.getElements(from, new boolean[][]{a}, (long)offset, (long)length);
    }

    public void removeElements(long var1, long var3);

    public void addElements(long var1, boolean[][] var3);

    public void addElements(long var1, boolean[][] var3, long var4, long var6);

    default public void setElements(boolean[][] a) {
        this.setElements(0L, a);
    }

    default public void setElements(long index, boolean[][] a) {
        this.setElements(index, a, 0L, BigArrays.length(a));
    }

    default public void setElements(long index, boolean[][] a, long offset, long length) {
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
        BooleanBigListIterator iter = this.listIterator(index);
        long i = 0L;
        while (i < length) {
            iter.nextBoolean();
            iter.set(BigArrays.get(a, offset + i++));
        }
    }

    @Override
    public void add(long var1, boolean var3);

    public boolean addAll(long var1, BooleanCollection var3);

    public boolean getBoolean(long var1);

    public boolean removeBoolean(long var1);

    @Override
    public boolean set(long var1, boolean var3);

    public long indexOf(boolean var1);

    public long lastIndexOf(boolean var1);

    @Override
    @Deprecated
    public void add(long var1, Boolean var3);

    @Override
    @Deprecated
    public Boolean get(long var1);

    @Override
    @Deprecated
    public long indexOf(Object var1);

    @Override
    @Deprecated
    public long lastIndexOf(Object var1);

    @Override
    @Deprecated
    public Boolean remove(long var1);

    @Override
    @Deprecated
    public Boolean set(long var1, Boolean var3);

    default public boolean addAll(long index, BooleanBigList l) {
        return this.addAll(index, (BooleanCollection)l);
    }

    default public boolean addAll(BooleanBigList l) {
        return this.addAll(this.size64(), l);
    }

    default public boolean addAll(long index, BooleanList l) {
        return this.addAll(index, (BooleanCollection)l);
    }

    default public boolean addAll(BooleanList l) {
        return this.addAll(this.size64(), l);
    }
}

