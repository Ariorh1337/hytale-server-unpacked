/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSets;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.util.Set;

public interface ShortSet
extends ShortCollection,
Set<Short> {
    @Override
    public ShortIterator iterator();

    @Override
    default public ShortSpliterator spliterator() {
        return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 321);
    }

    public boolean remove(short var1);

    @Override
    @Deprecated
    default public boolean remove(Object o) {
        return ShortCollection.super.remove(o);
    }

    @Override
    @Deprecated
    default public boolean add(Short o) {
        return ShortCollection.super.add(o);
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        return ShortCollection.super.contains(o);
    }

    @Override
    @Deprecated
    default public boolean rem(short k) {
        return this.remove(k);
    }

    public static ShortSet of() {
        return ShortSets.UNMODIFIABLE_EMPTY_SET;
    }

    public static ShortSet of(short e) {
        return ShortSets.singleton(e);
    }

    public static ShortSet of(short e0, short e1) {
        ShortArraySet innerSet = new ShortArraySet(2);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        return ShortSets.unmodifiable(innerSet);
    }

    public static ShortSet of(short e0, short e1, short e2) {
        ShortArraySet innerSet = new ShortArraySet(3);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        if (!innerSet.add(e2)) {
            throw new IllegalArgumentException("Duplicate element: " + e2);
        }
        return ShortSets.unmodifiable(innerSet);
    }

    public static ShortSet of(short ... a) {
        switch (a.length) {
            case 0: {
                return ShortSet.of();
            }
            case 1: {
                return ShortSet.of(a[0]);
            }
            case 2: {
                return ShortSet.of(a[0], a[1]);
            }
            case 3: {
                return ShortSet.of(a[0], a[1], a[2]);
            }
        }
        AbstractShortSet innerSet = a.length <= 4 ? new ShortArraySet(a.length) : new ShortOpenHashSet(a.length);
        for (short element : a) {
            if (innerSet.add(element)) continue;
            throw new IllegalArgumentException("Duplicate element: " + element);
        }
        return ShortSets.unmodifiable(innerSet);
    }
}

