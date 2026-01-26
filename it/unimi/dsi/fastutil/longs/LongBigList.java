/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.longs.LongBigListIterator;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;

public interface LongBigList
extends BigList<Long>,
LongCollection,
Comparable<BigList<? extends Long>> {
    @Override
    public LongBigListIterator iterator();

    public LongBigListIterator listIterator();

    public LongBigListIterator listIterator(long var1);

    @Override
    default public LongSpliterator spliterator() {
        return LongSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
    }

    public LongBigList subList(long var1, long var3);

    public void getElements(long var1, long[][] var3, long var4, long var6);

    default public void getElements(long from, long[] a, int offset, int length) {
        this.getElements(from, new long[][]{a}, (long)offset, (long)length);
    }

    public void removeElements(long var1, long var3);

    public void addElements(long var1, long[][] var3);

    public void addElements(long var1, long[][] var3, long var4, long var6);

    default public void setElements(long[][] a) {
        this.setElements(0L, a);
    }

    default public void setElements(long index, long[][] a) {
        this.setElements(index, a, 0L, BigArrays.length(a));
    }

    default public void setElements(long index, long[][] a, long offset, long length) {
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
        LongBigListIterator iter = this.listIterator(index);
        long i = 0L;
        while (i < length) {
            iter.nextLong();
            iter.set(BigArrays.get(a, offset + i++));
        }
    }

    @Override
    public void add(long var1, long var3);

    public boolean addAll(long var1, LongCollection var3);

    public long getLong(long var1);

    public long removeLong(long var1);

    @Override
    public long set(long var1, long var3);

    public long indexOf(long var1);

    public long lastIndexOf(long var1);

    @Override
    @Deprecated
    public void add(long var1, Long var3);

    @Override
    @Deprecated
    public Long get(long var1);

    @Override
    @Deprecated
    public long indexOf(Object var1);

    @Override
    @Deprecated
    public long lastIndexOf(Object var1);

    @Override
    @Deprecated
    public Long remove(long var1);

    @Override
    @Deprecated
    public Long set(long var1, Long var3);

    default public boolean addAll(long index, LongBigList l) {
        return this.addAll(index, (LongCollection)l);
    }

    default public boolean addAll(LongBigList l) {
        return this.addAll(this.size64(), l);
    }

    default public boolean addAll(long index, LongList l) {
        return this.addAll(index, (LongCollection)l);
    }

    default public boolean addAll(LongList l) {
        return this.addAll(this.size64(), l);
    }
}

