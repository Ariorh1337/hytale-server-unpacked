/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongReferenceImmutablePair;

public interface LongReferencePair<V>
extends Pair<Long, V> {
    public long leftLong();

    @Override
    @Deprecated
    default public Long left() {
        return this.leftLong();
    }

    default public LongReferencePair<V> left(long l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongReferencePair<V> left(Long l) {
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

    default public LongReferencePair<V> first(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongReferencePair<V> first(Long l) {
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

    default public LongReferencePair<V> key(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongReferencePair<V> key(Long l) {
        return this.key((long)l);
    }

    public static <V> LongReferencePair<V> of(long left, V right) {
        return new LongReferenceImmutablePair<V>(left, right);
    }
}

