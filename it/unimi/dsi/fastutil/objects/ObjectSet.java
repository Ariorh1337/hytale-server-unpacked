/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.util.Set;

public interface ObjectSet<K>
extends ObjectCollection<K>,
Set<K> {
    @Override
    public ObjectIterator<K> iterator();

    @Override
    default public ObjectSpliterator<K> spliterator() {
        return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 65);
    }

    public static <K> ObjectSet<K> of() {
        return ObjectSets.UNMODIFIABLE_EMPTY_SET;
    }

    public static <K> ObjectSet<K> of(K e) {
        return ObjectSets.singleton(e);
    }

    public static <K> ObjectSet<K> of(K e0, K e1) {
        ObjectArraySet<K> innerSet = new ObjectArraySet<K>(2);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        return ObjectSets.unmodifiable(innerSet);
    }

    public static <K> ObjectSet<K> of(K e0, K e1, K e2) {
        ObjectArraySet<K> innerSet = new ObjectArraySet<K>(3);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        if (!innerSet.add(e2)) {
            throw new IllegalArgumentException("Duplicate element: " + e2);
        }
        return ObjectSets.unmodifiable(innerSet);
    }

    @SafeVarargs
    public static <K> ObjectSet<K> of(K ... a) {
        switch (a.length) {
            case 0: {
                return ObjectSet.of();
            }
            case 1: {
                return ObjectSet.of(a[0]);
            }
            case 2: {
                return ObjectSet.of(a[0], a[1]);
            }
            case 3: {
                return ObjectSet.of(a[0], a[1], a[2]);
            }
        }
        AbstractObjectSet innerSet = a.length <= 4 ? new ObjectArraySet(a.length) : new ObjectOpenHashSet(a.length);
        for (K element : a) {
            if (innerSet.add(element)) continue;
            throw new IllegalArgumentException("Duplicate element: " + element);
        }
        return ObjectSets.unmodifiable(innerSet);
    }
}

