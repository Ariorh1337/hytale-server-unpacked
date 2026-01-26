/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import java.util.Set;

public interface LongSet
extends LongCollection,
Set<Long> {
    @Override
    public LongIterator iterator();

    @Override
    default public LongSpliterator spliterator() {
        return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 321);
    }

    public boolean remove(long var1);

    @Override
    @Deprecated
    default public boolean remove(Object o) {
        return LongCollection.super.remove(o);
    }

    @Override
    @Deprecated
    default public boolean add(Long o) {
        return LongCollection.super.add(o);
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        return LongCollection.super.contains(o);
    }

    @Override
    @Deprecated
    default public boolean rem(long k) {
        return this.remove(k);
    }

    public static LongSet of() {
        return LongSets.UNMODIFIABLE_EMPTY_SET;
    }

    public static LongSet of(long e) {
        return LongSets.singleton(e);
    }

    public static LongSet of(long e0, long e1) {
        LongArraySet innerSet = new LongArraySet(2);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        return LongSets.unmodifiable(innerSet);
    }

    public static LongSet of(long e0, long e1, long e2) {
        LongArraySet innerSet = new LongArraySet(3);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        if (!innerSet.add(e2)) {
            throw new IllegalArgumentException("Duplicate element: " + e2);
        }
        return LongSets.unmodifiable(innerSet);
    }

    public static LongSet of(long ... a) {
        switch (a.length) {
            case 0: {
                return LongSet.of();
            }
            case 1: {
                return LongSet.of(a[0]);
            }
            case 2: {
                return LongSet.of(a[0], a[1]);
            }
            case 3: {
                return LongSet.of(a[0], a[1], a[2]);
            }
        }
        AbstractLongSet innerSet = a.length <= 4 ? new LongArraySet(a.length) : new LongOpenHashSet(a.length);
        for (long element : a) {
            if (innerSet.add(element)) continue;
            throw new IllegalArgumentException("Duplicate element: " + element);
        }
        return LongSets.unmodifiable(innerSet);
    }
}

