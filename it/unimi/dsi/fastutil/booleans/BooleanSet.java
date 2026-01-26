/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanSet;
import it.unimi.dsi.fastutil.booleans.BooleanArraySet;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanOpenHashSet;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import java.util.Set;

public interface BooleanSet
extends BooleanCollection,
Set<Boolean> {
    @Override
    public BooleanIterator iterator();

    @Override
    default public BooleanSpliterator spliterator() {
        return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 321);
    }

    public boolean remove(boolean var1);

    @Override
    @Deprecated
    default public boolean remove(Object o) {
        return BooleanCollection.super.remove(o);
    }

    @Override
    @Deprecated
    default public boolean add(Boolean o) {
        return BooleanCollection.super.add(o);
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        return BooleanCollection.super.contains(o);
    }

    @Override
    @Deprecated
    default public boolean rem(boolean k) {
        return this.remove(k);
    }

    public static BooleanSet of() {
        return BooleanSets.UNMODIFIABLE_EMPTY_SET;
    }

    public static BooleanSet of(boolean e) {
        return BooleanSets.singleton(e);
    }

    public static BooleanSet of(boolean e0, boolean e1) {
        BooleanArraySet innerSet = new BooleanArraySet(2);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        return BooleanSets.unmodifiable(innerSet);
    }

    public static BooleanSet of(boolean e0, boolean e1, boolean e2) {
        BooleanArraySet innerSet = new BooleanArraySet(3);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        if (!innerSet.add(e2)) {
            throw new IllegalArgumentException("Duplicate element: " + e2);
        }
        return BooleanSets.unmodifiable(innerSet);
    }

    public static BooleanSet of(boolean ... a) {
        switch (a.length) {
            case 0: {
                return BooleanSet.of();
            }
            case 1: {
                return BooleanSet.of(a[0]);
            }
            case 2: {
                return BooleanSet.of(a[0], a[1]);
            }
            case 3: {
                return BooleanSet.of(a[0], a[1], a[2]);
            }
        }
        AbstractBooleanSet innerSet = a.length <= 4 ? new BooleanArraySet(a.length) : new BooleanOpenHashSet(a.length);
        for (boolean element : a) {
            if (innerSet.add(element)) continue;
            throw new IllegalArgumentException("Duplicate element: " + element);
        }
        return BooleanSets.unmodifiable(innerSet);
    }
}

