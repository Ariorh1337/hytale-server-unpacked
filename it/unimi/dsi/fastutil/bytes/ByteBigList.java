/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.bytes.ByteBigListIterator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;

public interface ByteBigList
extends BigList<Byte>,
ByteCollection,
Comparable<BigList<? extends Byte>> {
    @Override
    public ByteBigListIterator iterator();

    public ByteBigListIterator listIterator();

    public ByteBigListIterator listIterator(long var1);

    @Override
    default public ByteSpliterator spliterator() {
        return ByteSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
    }

    public ByteBigList subList(long var1, long var3);

    public void getElements(long var1, byte[][] var3, long var4, long var6);

    default public void getElements(long from, byte[] a, int offset, int length) {
        this.getElements(from, new byte[][]{a}, (long)offset, (long)length);
    }

    public void removeElements(long var1, long var3);

    public void addElements(long var1, byte[][] var3);

    public void addElements(long var1, byte[][] var3, long var4, long var6);

    default public void setElements(byte[][] a) {
        this.setElements(0L, a);
    }

    default public void setElements(long index, byte[][] a) {
        this.setElements(index, a, 0L, BigArrays.length(a));
    }

    default public void setElements(long index, byte[][] a, long offset, long length) {
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
        ByteBigListIterator iter = this.listIterator(index);
        long i = 0L;
        while (i < length) {
            iter.nextByte();
            iter.set(BigArrays.get(a, offset + i++));
        }
    }

    @Override
    public void add(long var1, byte var3);

    public boolean addAll(long var1, ByteCollection var3);

    public byte getByte(long var1);

    public byte removeByte(long var1);

    @Override
    public byte set(long var1, byte var3);

    public long indexOf(byte var1);

    public long lastIndexOf(byte var1);

    @Override
    @Deprecated
    public void add(long var1, Byte var3);

    @Override
    @Deprecated
    public Byte get(long var1);

    @Override
    @Deprecated
    public long indexOf(Object var1);

    @Override
    @Deprecated
    public long lastIndexOf(Object var1);

    @Override
    @Deprecated
    public Byte remove(long var1);

    @Override
    @Deprecated
    public Byte set(long var1, Byte var3);

    default public boolean addAll(long index, ByteBigList l) {
        return this.addAll(index, (ByteCollection)l);
    }

    default public boolean addAll(ByteBigList l) {
        return this.addAll(this.size64(), l);
    }

    default public boolean addAll(long index, ByteList l) {
        return this.addAll(index, (ByteCollection)l);
    }

    default public boolean addAll(ByteList l) {
        return this.addAll(this.size64(), l);
    }
}

