/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.chars.CharBigListIterator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;

public interface CharBigList
extends BigList<Character>,
CharCollection,
Comparable<BigList<? extends Character>> {
    @Override
    public CharBigListIterator iterator();

    public CharBigListIterator listIterator();

    public CharBigListIterator listIterator(long var1);

    @Override
    default public CharSpliterator spliterator() {
        return CharSpliterators.asSpliterator(this.iterator(), this.size64(), 16720);
    }

    public CharBigList subList(long var1, long var3);

    public void getElements(long var1, char[][] var3, long var4, long var6);

    default public void getElements(long from, char[] a, int offset, int length) {
        this.getElements(from, new char[][]{a}, (long)offset, (long)length);
    }

    public void removeElements(long var1, long var3);

    public void addElements(long var1, char[][] var3);

    public void addElements(long var1, char[][] var3, long var4, long var6);

    default public void setElements(char[][] a) {
        this.setElements(0L, a);
    }

    default public void setElements(long index, char[][] a) {
        this.setElements(index, a, 0L, BigArrays.length(a));
    }

    default public void setElements(long index, char[][] a, long offset, long length) {
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
        CharBigListIterator iter = this.listIterator(index);
        long i = 0L;
        while (i < length) {
            iter.nextChar();
            iter.set(BigArrays.get(a, offset + i++));
        }
    }

    @Override
    public void add(long var1, char var3);

    public boolean addAll(long var1, CharCollection var3);

    public char getChar(long var1);

    public char removeChar(long var1);

    @Override
    public char set(long var1, char var3);

    public long indexOf(char var1);

    public long lastIndexOf(char var1);

    @Override
    @Deprecated
    public void add(long var1, Character var3);

    @Override
    @Deprecated
    public Character get(long var1);

    @Override
    @Deprecated
    public long indexOf(Object var1);

    @Override
    @Deprecated
    public long lastIndexOf(Object var1);

    @Override
    @Deprecated
    public Character remove(long var1);

    @Override
    @Deprecated
    public Character set(long var1, Character var3);

    default public boolean addAll(long index, CharBigList l) {
        return this.addAll(index, (CharCollection)l);
    }

    default public boolean addAll(CharBigList l) {
        return this.addAll(this.size64(), l);
    }

    default public boolean addAll(long index, CharList l) {
        return this.addAll(index, (CharCollection)l);
    }

    default public boolean addAll(CharList l) {
        return this.addAll(this.size64(), l);
    }
}

