/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.ByteArraySet;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import java.util.Set;

public interface ByteSet
extends ByteCollection,
Set<Byte> {
    @Override
    public ByteIterator iterator();

    @Override
    default public ByteSpliterator spliterator() {
        return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 321);
    }

    public boolean remove(byte var1);

    @Override
    @Deprecated
    default public boolean remove(Object o) {
        return ByteCollection.super.remove(o);
    }

    @Override
    @Deprecated
    default public boolean add(Byte o) {
        return ByteCollection.super.add(o);
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        return ByteCollection.super.contains(o);
    }

    @Override
    @Deprecated
    default public boolean rem(byte k) {
        return this.remove(k);
    }

    public static ByteSet of() {
        return ByteSets.UNMODIFIABLE_EMPTY_SET;
    }

    public static ByteSet of(byte e) {
        return ByteSets.singleton(e);
    }

    public static ByteSet of(byte e0, byte e1) {
        ByteArraySet innerSet = new ByteArraySet(2);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        return ByteSets.unmodifiable(innerSet);
    }

    public static ByteSet of(byte e0, byte e1, byte e2) {
        ByteArraySet innerSet = new ByteArraySet(3);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        if (!innerSet.add(e2)) {
            throw new IllegalArgumentException("Duplicate element: " + e2);
        }
        return ByteSets.unmodifiable(innerSet);
    }

    public static ByteSet of(byte ... a) {
        switch (a.length) {
            case 0: {
                return ByteSet.of();
            }
            case 1: {
                return ByteSet.of(a[0]);
            }
            case 2: {
                return ByteSet.of(a[0], a[1]);
            }
            case 3: {
                return ByteSet.of(a[0], a[1], a[2]);
            }
        }
        AbstractByteSet innerSet = a.length <= 4 ? new ByteArraySet(a.length) : new ByteOpenHashSet(a.length);
        for (byte element : a) {
            if (innerSet.add(element)) continue;
            throw new IllegalArgumentException("Duplicate element: " + element);
        }
        return ByteSets.unmodifiable(innerSet);
    }
}

