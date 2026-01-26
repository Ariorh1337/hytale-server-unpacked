/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.shorts.ShortBigListIterator;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;

public interface ShortBigList
extends BigList<Short>,
ShortCollection,
Comparable<BigList<? extends Short>> {
    @Override
    public ShortBigListIterator iterator();

    public ShortBigListIterator listIterator();

    public ShortBigListIterator listIterator(long var1);

    @Override
    default public ShortSpliterator spliterator() {
        return ShortSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
    }

    public ShortBigList subList(long var1, long var3);

    public void getElements(long var1, short[][] var3, long var4, long var6);

    default public void getElements(long from, short[] a, int offset, int length) {
        this.getElements(from, new short[][]{a}, (long)offset, (long)length);
    }

    public void removeElements(long var1, long var3);

    public void addElements(long var1, short[][] var3);

    public void addElements(long var1, short[][] var3, long var4, long var6);

    default public void setElements(short[][] a) {
        this.setElements(0L, a);
    }

    default public void setElements(long index, short[][] a) {
        this.setElements(index, a, 0L, BigArrays.length(a));
    }

    default public void setElements(long index, short[][] a, long offset, long length) {
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
        ShortBigListIterator iter = this.listIterator(index);
        long i = 0L;
        while (i < length) {
            iter.nextShort();
            iter.set(BigArrays.get(a, offset + i++));
        }
    }

    @Override
    public void add(long var1, short var3);

    public boolean addAll(long var1, ShortCollection var3);

    public short getShort(long var1);

    public short removeShort(long var1);

    @Override
    public short set(long var1, short var3);

    public long indexOf(short var1);

    public long lastIndexOf(short var1);

    @Override
    @Deprecated
    public void add(long var1, Short var3);

    @Override
    @Deprecated
    public Short get(long var1);

    @Override
    @Deprecated
    public long indexOf(Object var1);

    @Override
    @Deprecated
    public long lastIndexOf(Object var1);

    @Override
    @Deprecated
    public Short remove(long var1);

    @Override
    @Deprecated
    public Short set(long var1, Short var3);

    default public boolean addAll(long index, ShortBigList l) {
        return this.addAll(index, (ShortCollection)l);
    }

    default public boolean addAll(ShortBigList l) {
        return this.addAll(this.size64(), l);
    }

    default public boolean addAll(long index, ShortList l) {
        return this.addAll(index, (ShortCollection)l);
    }

    default public boolean addAll(ShortList l) {
        return this.addAll(this.size64(), l);
    }
}

