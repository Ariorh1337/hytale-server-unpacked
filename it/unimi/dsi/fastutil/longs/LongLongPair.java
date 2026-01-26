/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;
import java.util.Comparator;

public interface LongLongPair
extends Pair<Long, Long> {
    public long leftLong();

    @Override
    @Deprecated
    default public Long left() {
        return this.leftLong();
    }

    default public LongLongPair left(long l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongLongPair left(Long l) {
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

    default public LongLongPair first(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongLongPair first(Long l) {
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

    default public LongLongPair key(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongLongPair key(Long l) {
        return this.key((long)l);
    }

    public long rightLong();

    @Override
    @Deprecated
    default public Long right() {
        return this.rightLong();
    }

    default public LongLongPair right(long r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongLongPair right(Long l) {
        return this.right((long)l);
    }

    default public long secondLong() {
        return this.rightLong();
    }

    @Override
    @Deprecated
    default public Long second() {
        return this.secondLong();
    }

    default public LongLongPair second(long r) {
        return this.right(r);
    }

    @Deprecated
    default public LongLongPair second(Long l) {
        return this.second((long)l);
    }

    default public long valueLong() {
        return this.rightLong();
    }

    @Override
    @Deprecated
    default public Long value() {
        return this.valueLong();
    }

    default public LongLongPair value(long r) {
        return this.right(r);
    }

    @Deprecated
    default public LongLongPair value(Long l) {
        return this.value((long)l);
    }

    public static LongLongPair of(long left, long right) {
        return new LongLongImmutablePair(left, right);
    }

    public static Comparator<LongLongPair> lexComparator() {
        return (x, y) -> {
            int t = Long.compare(x.leftLong(), y.leftLong());
            if (t != 0) {
                return t;
            }
            return Long.compare(x.rightLong(), y.rightLong());
        };
    }
}

