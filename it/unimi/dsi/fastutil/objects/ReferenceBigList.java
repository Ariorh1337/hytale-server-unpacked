/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.objects.ObjectBigListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceList;

public interface ReferenceBigList<K>
extends BigList<K>,
ReferenceCollection<K> {
    @Override
    public ObjectBigListIterator<K> iterator();

    @Override
    public ObjectBigListIterator<K> listIterator();

    @Override
    public ObjectBigListIterator<K> listIterator(long var1);

    @Override
    default public ObjectSpliterator<K> spliterator() {
        return ObjectSpliterators.asSpliterator(this.iterator(), this.size64(), 16464);
    }

    @Override
    public ReferenceBigList<K> subList(long var1, long var3);

    public void getElements(long var1, Object[][] var3, long var4, long var6);

    default public void getElements(long from, Object[] a, int offset, int length) {
        this.getElements(from, new Object[][]{a}, (long)offset, (long)length);
    }

    public void removeElements(long var1, long var3);

    public void addElements(long var1, K[][] var3);

    public void addElements(long var1, K[][] var3, long var4, long var6);

    default public void setElements(K[][] a) {
        this.setElements(0L, a);
    }

    default public void setElements(long index, K[][] a) {
        this.setElements(index, a, 0L, BigArrays.length(a));
    }

    default public void setElements(long index, K[][] a, long offset, long length) {
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
        BigListIterator iter = this.listIterator(index);
        long i = 0L;
        while (i < length) {
            iter.next();
            iter.set(BigArrays.get(a, offset + i++));
        }
    }

    @Override
    default public boolean addAll(long index, ReferenceBigList<? extends K> l) {
        return this.addAll(index, l);
    }

    @Override
    default public boolean addAll(ReferenceBigList<? extends K> l) {
        return this.addAll(this.size64(), l);
    }

    @Override
    default public boolean addAll(long index, ReferenceList<? extends K> l) {
        return this.addAll(index, l);
    }

    @Override
    default public boolean addAll(ReferenceList<? extends K> l) {
        return this.addAll(this.size64(), l);
    }
}

