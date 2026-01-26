/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSets;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import java.util.Set;

public interface CharSet
extends CharCollection,
Set<Character> {
    @Override
    public CharIterator iterator();

    @Override
    default public CharSpliterator spliterator() {
        return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 321);
    }

    public boolean remove(char var1);

    @Override
    @Deprecated
    default public boolean remove(Object o) {
        return CharCollection.super.remove(o);
    }

    @Override
    @Deprecated
    default public boolean add(Character o) {
        return CharCollection.super.add(o);
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        return CharCollection.super.contains(o);
    }

    @Override
    @Deprecated
    default public boolean rem(char k) {
        return this.remove(k);
    }

    public static CharSet of() {
        return CharSets.UNMODIFIABLE_EMPTY_SET;
    }

    public static CharSet of(char e) {
        return CharSets.singleton(e);
    }

    public static CharSet of(char e0, char e1) {
        CharArraySet innerSet = new CharArraySet(2);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        return CharSets.unmodifiable(innerSet);
    }

    public static CharSet of(char e0, char e1, char e2) {
        CharArraySet innerSet = new CharArraySet(3);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        if (!innerSet.add(e2)) {
            throw new IllegalArgumentException("Duplicate element: " + e2);
        }
        return CharSets.unmodifiable(innerSet);
    }

    public static CharSet of(char ... a) {
        switch (a.length) {
            case 0: {
                return CharSet.of();
            }
            case 1: {
                return CharSet.of(a[0]);
            }
            case 2: {
                return CharSet.of(a[0], a[1]);
            }
            case 3: {
                return CharSet.of(a[0], a[1], a[2]);
            }
        }
        AbstractCharSet innerSet = a.length <= 4 ? new CharArraySet(a.length) : new CharOpenHashSet(a.length);
        for (char element : a) {
            if (innerSet.add(element)) continue;
            throw new IllegalArgumentException("Duplicate element: " + element);
        }
        return CharSets.unmodifiable(innerSet);
    }
}

