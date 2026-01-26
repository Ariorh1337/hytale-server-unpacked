/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatSets;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import java.util.Set;

public interface FloatSet
extends FloatCollection,
Set<Float> {
    @Override
    public FloatIterator iterator();

    @Override
    default public FloatSpliterator spliterator() {
        return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 321);
    }

    public boolean remove(float var1);

    @Override
    @Deprecated
    default public boolean remove(Object o) {
        return FloatCollection.super.remove(o);
    }

    @Override
    @Deprecated
    default public boolean add(Float o) {
        return FloatCollection.super.add(o);
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        return FloatCollection.super.contains(o);
    }

    @Override
    @Deprecated
    default public boolean rem(float k) {
        return this.remove(k);
    }

    public static FloatSet of() {
        return FloatSets.UNMODIFIABLE_EMPTY_SET;
    }

    public static FloatSet of(float e) {
        return FloatSets.singleton(e);
    }

    public static FloatSet of(float e0, float e1) {
        FloatArraySet innerSet = new FloatArraySet(2);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        return FloatSets.unmodifiable(innerSet);
    }

    public static FloatSet of(float e0, float e1, float e2) {
        FloatArraySet innerSet = new FloatArraySet(3);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        if (!innerSet.add(e2)) {
            throw new IllegalArgumentException("Duplicate element: " + e2);
        }
        return FloatSets.unmodifiable(innerSet);
    }

    public static FloatSet of(float ... a) {
        switch (a.length) {
            case 0: {
                return FloatSet.of();
            }
            case 1: {
                return FloatSet.of(a[0]);
            }
            case 2: {
                return FloatSet.of(a[0], a[1]);
            }
            case 3: {
                return FloatSet.of(a[0], a[1], a[2]);
            }
        }
        AbstractFloatSet innerSet = a.length <= 4 ? new FloatArraySet(a.length) : new FloatOpenHashSet(a.length);
        for (float element : a) {
            if (innerSet.add(element)) continue;
            throw new IllegalArgumentException("Duplicate element: " + element);
        }
        return FloatSets.unmodifiable(innerSet);
    }
}

