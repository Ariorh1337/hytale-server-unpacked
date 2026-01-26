/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.bytes.AbstractByteList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteComparators;
import it.unimi.dsi.fastutil.bytes.ByteImmutableList;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteLists;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import it.unimi.dsi.fastutil.bytes.ByteUnaryOperator;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

public interface ByteList
extends List<Byte>,
Comparable<List<? extends Byte>>,
ByteCollection {
    @Override
    public ByteListIterator iterator();

    @Override
    default public ByteSpliterator spliterator() {
        if (this instanceof RandomAccess) {
            return new AbstractByteList.IndexBasedSpliterator(this, 0);
        }
        return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 16720);
    }

    public ByteListIterator listIterator();

    public ByteListIterator listIterator(int var1);

    public ByteList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, byte[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, byte[] var2);

    public void addElements(int var1, byte[] var2, int var3, int var4);

    default public void setElements(byte[] a) {
        this.setElements(0, a);
    }

    default public void setElements(int index, byte[] a) {
        this.setElements(index, a, 0, a.length);
    }

    default public void setElements(int index, byte[] a, int offset, int length) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
        }
        ByteArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
        }
        ByteListIterator iter = this.listIterator(index);
        int i = 0;
        while (i < length) {
            iter.nextByte();
            iter.set(a[offset + i++]);
        }
    }

    @Override
    public boolean add(byte var1);

    @Override
    public void add(int var1, byte var2);

    @Override
    @Deprecated
    default public void add(int index, Byte key) {
        this.add(index, (byte)key);
    }

    public boolean addAll(int var1, ByteCollection var2);

    @Override
    public byte set(int var1, byte var2);

    default public void replaceAll(ByteUnaryOperator operator) {
        ByteListIterator iter = this.listIterator();
        while (iter.hasNext()) {
            iter.set(operator.apply(iter.nextByte()));
        }
    }

    default public void replaceAll(IntUnaryOperator operator) {
        this.replaceAll(operator instanceof ByteUnaryOperator ? (ByteUnaryOperator)operator : x -> SafeMath.safeIntToByte(operator.applyAsInt(x)));
    }

    @Override
    @Deprecated
    default public void replaceAll(UnaryOperator<Byte> operator) {
        Objects.requireNonNull(operator);
        this.replaceAll(operator instanceof ByteUnaryOperator ? (ByteUnaryOperator)operator : operator::apply);
    }

    public byte getByte(int var1);

    public int indexOf(byte var1);

    public int lastIndexOf(byte var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return ByteCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Byte get(int index) {
        return this.getByte(index);
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf((Byte)o);
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf((Byte)o);
    }

    @Override
    @Deprecated
    default public boolean add(Byte k) {
        return this.add((byte)k);
    }

    public byte removeByte(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return ByteCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Byte remove(int index) {
        return this.removeByte(index);
    }

    @Override
    @Deprecated
    default public Byte set(int index, Byte k) {
        return this.set(index, (byte)k);
    }

    default public boolean addAll(int index, ByteList l) {
        return this.addAll(index, (ByteCollection)l);
    }

    default public boolean addAll(ByteList l) {
        return this.addAll(this.size(), l);
    }

    public static ByteList of() {
        return ByteImmutableList.of();
    }

    public static ByteList of(byte e) {
        return ByteLists.singleton(e);
    }

    public static ByteList of(byte e0, byte e1) {
        return ByteImmutableList.of(new byte[]{e0, e1});
    }

    public static ByteList of(byte e0, byte e1, byte e2) {
        return ByteImmutableList.of(new byte[]{e0, e1, e2});
    }

    public static ByteList of(byte ... a) {
        switch (a.length) {
            case 0: {
                return ByteList.of();
            }
            case 1: {
                return ByteList.of(a[0]);
            }
        }
        return ByteImmutableList.of(a);
    }

    @Override
    @Deprecated
    default public void sort(Comparator<? super Byte> comparator) {
        this.sort(ByteComparators.asByteComparator(comparator));
    }

    default public void sort(ByteComparator comparator) {
        if (comparator == null) {
            this.unstableSort(comparator);
        } else {
            byte[] elements = this.toByteArray();
            ByteArrays.stableSort(elements, comparator);
            this.setElements(elements);
        }
    }

    @Deprecated
    default public void unstableSort(Comparator<? super Byte> comparator) {
        this.unstableSort(ByteComparators.asByteComparator(comparator));
    }

    default public void unstableSort(ByteComparator comparator) {
        byte[] elements = this.toByteArray();
        if (comparator == null) {
            ByteArrays.unstableSort(elements);
        } else {
            ByteArrays.unstableSort(elements, comparator);
        }
        this.setElements(elements);
    }
}

