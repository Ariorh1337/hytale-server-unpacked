/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.chars.AbstractCharList;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharComparators;
import it.unimi.dsi.fastutil.chars.CharImmutableList;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.chars.CharLists;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import it.unimi.dsi.fastutil.chars.CharUnaryOperator;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

public interface CharList
extends List<Character>,
Comparable<List<? extends Character>>,
CharCollection {
    @Override
    public CharListIterator iterator();

    @Override
    default public CharSpliterator spliterator() {
        if (this instanceof RandomAccess) {
            return new AbstractCharList.IndexBasedSpliterator(this, 0);
        }
        return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 16720);
    }

    public CharListIterator listIterator();

    public CharListIterator listIterator(int var1);

    public CharList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, char[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, char[] var2);

    public void addElements(int var1, char[] var2, int var3, int var4);

    default public void setElements(char[] a) {
        this.setElements(0, a);
    }

    default public void setElements(int index, char[] a) {
        this.setElements(index, a, 0, a.length);
    }

    default public void setElements(int index, char[] a, int offset, int length) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
        }
        CharArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
        }
        CharListIterator iter = this.listIterator(index);
        int i = 0;
        while (i < length) {
            iter.nextChar();
            iter.set(a[offset + i++]);
        }
    }

    @Override
    public boolean add(char var1);

    @Override
    public void add(int var1, char var2);

    @Override
    @Deprecated
    default public void add(int index, Character key) {
        this.add(index, key.charValue());
    }

    public boolean addAll(int var1, CharCollection var2);

    @Override
    public char set(int var1, char var2);

    default public void replaceAll(CharUnaryOperator operator) {
        CharListIterator iter = this.listIterator();
        while (iter.hasNext()) {
            iter.set(operator.apply(iter.nextChar()));
        }
    }

    default public void replaceAll(IntUnaryOperator operator) {
        this.replaceAll(operator instanceof CharUnaryOperator ? (CharUnaryOperator)operator : x -> SafeMath.safeIntToChar(operator.applyAsInt(x)));
    }

    @Override
    @Deprecated
    default public void replaceAll(UnaryOperator<Character> operator) {
        Objects.requireNonNull(operator);
        this.replaceAll(operator instanceof CharUnaryOperator ? (CharUnaryOperator)operator : operator::apply);
    }

    public char getChar(int var1);

    public int indexOf(char var1);

    public int lastIndexOf(char var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return CharCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Character get(int index) {
        return Character.valueOf(this.getChar(index));
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf(((Character)o).charValue());
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf(((Character)o).charValue());
    }

    @Override
    @Deprecated
    default public boolean add(Character k) {
        return this.add(k.charValue());
    }

    public char removeChar(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return CharCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Character remove(int index) {
        return Character.valueOf(this.removeChar(index));
    }

    @Override
    @Deprecated
    default public Character set(int index, Character k) {
        return Character.valueOf(this.set(index, k.charValue()));
    }

    default public boolean addAll(int index, CharList l) {
        return this.addAll(index, (CharCollection)l);
    }

    default public boolean addAll(CharList l) {
        return this.addAll(this.size(), l);
    }

    public static CharList of() {
        return CharImmutableList.of();
    }

    public static CharList of(char e) {
        return CharLists.singleton(e);
    }

    public static CharList of(char e0, char e1) {
        return CharImmutableList.of(new char[]{e0, e1});
    }

    public static CharList of(char e0, char e1, char e2) {
        return CharImmutableList.of(new char[]{e0, e1, e2});
    }

    public static CharList of(char ... a) {
        switch (a.length) {
            case 0: {
                return CharList.of();
            }
            case 1: {
                return CharList.of(a[0]);
            }
        }
        return CharImmutableList.of(a);
    }

    @Override
    @Deprecated
    default public void sort(Comparator<? super Character> comparator) {
        this.sort(CharComparators.asCharComparator(comparator));
    }

    default public void sort(CharComparator comparator) {
        if (comparator == null) {
            this.unstableSort(comparator);
        } else {
            char[] elements = this.toCharArray();
            CharArrays.stableSort(elements, comparator);
            this.setElements(elements);
        }
    }

    @Deprecated
    default public void unstableSort(Comparator<? super Character> comparator) {
        this.unstableSort(CharComparators.asCharComparator(comparator));
    }

    default public void unstableSort(CharComparator comparator) {
        char[] elements = this.toCharArray();
        if (comparator == null) {
            CharArrays.unstableSort(elements);
        } else {
            CharArrays.unstableSort(elements, comparator);
        }
        this.setElements(elements);
    }
}

