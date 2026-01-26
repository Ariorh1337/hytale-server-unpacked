/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import java.util.Set;

public interface ReferenceSet<K>
extends ReferenceCollection<K>,
Set<K> {
    @Override
    public ObjectIterator<K> iterator();

    @Override
    default public ObjectSpliterator<K> spliterator() {
        return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 65);
    }

    public static <K> ReferenceSet<K> of() {
        return ReferenceSets.UNMODIFIABLE_EMPTY_SET;
    }

    public static <K> ReferenceSet<K> of(K e) {
        return ReferenceSets.singleton(e);
    }

    public static <K> ReferenceSet<K> of(K e0, K e1) {
        ReferenceArraySet<K> innerSet = new ReferenceArraySet<K>(2);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        return ReferenceSets.unmodifiable(innerSet);
    }

    public static <K> ReferenceSet<K> of(K e0, K e1, K e2) {
        ReferenceArraySet<K> innerSet = new ReferenceArraySet<K>(3);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        if (!innerSet.add(e2)) {
            throw new IllegalArgumentException("Duplicate element: " + e2);
        }
        return ReferenceSets.unmodifiable(innerSet);
    }

    @SafeVarargs
    public static <K> ReferenceSet<K> of(K ... a) {
        switch (a.length) {
            case 0: {
                return ReferenceSet.of();
            }
            case 1: {
                return ReferenceSet.of(a[0]);
            }
            case 2: {
                return ReferenceSet.of(a[0], a[1]);
            }
            case 3: {
                return ReferenceSet.of(a[0], a[1], a[2]);
            }
        }
        AbstractReferenceSet innerSet = a.length <= 4 ? new ReferenceArraySet(a.length) : new ReferenceOpenHashSet(a.length);
        for (K element : a) {
            if (innerSet.add(element)) continue;
            throw new IllegalArgumentException("Duplicate element: " + element);
        }
        return ReferenceSets.unmodifiable(innerSet);
    }
}

