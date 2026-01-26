/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongObjectImmutablePair;
import java.util.Comparator;

public interface LongObjectPair<V>
extends Pair<Long, V> {
    public long leftLong();

    @Override
    @Deprecated
    default public Long left() {
        return this.leftLong();
    }

    default public LongObjectPair<V> left(long l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongObjectPair<V> left(Long l) {
        return this.left((long)l);
    }

    default public long firstLong() {
        return this.leftLong();
    }

    @Override
    @Deprecated
    default public Long first() {
        return this.firstLong();
    }

    default public LongObjectPair<V> first(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongObjectPair<V> first(Long l) {
        return this.first((long)l);
    }

    default public long keyLong() {
        return this.firstLong();
    }

    @Override
    @Deprecated
    default public Long key() {
        return this.keyLong();
    }

    default public LongObjectPair<V> key(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongObjectPair<V> key(Long l) {
        return this.key((long)l);
    }

    public static <V> LongObjectPair<V> of(long left, V right) {
        return new LongObjectImmutablePair<V>(left, right);
    }

    public static <V> Comparator<LongObjectPair<V>> lexComparator() {
        return (x, y) -> {
            int t = Long.compare(x.leftLong(), y.leftLong());
            if (t != 0) {
                return t;
            }
            return ((Comparable)x.right()).compareTo(y.right());
        };
    }
}

