/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongIntImmutablePair;
import java.util.Comparator;

public interface LongIntPair
extends Pair<Long, Integer> {
    public long leftLong();

    @Override
    @Deprecated
    default public Long left() {
        return this.leftLong();
    }

    default public LongIntPair left(long l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongIntPair left(Long l) {
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

    default public LongIntPair first(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongIntPair first(Long l) {
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

    default public LongIntPair key(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongIntPair key(Long l) {
        return this.key((long)l);
    }

    public int rightInt();

    @Override
    @Deprecated
    default public Integer right() {
        return this.rightInt();
    }

    default public LongIntPair right(int r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongIntPair right(Integer l) {
        return this.right((int)l);
    }

    default public int secondInt() {
        return this.rightInt();
    }

    @Override
    @Deprecated
    default public Integer second() {
        return this.secondInt();
    }

    default public LongIntPair second(int r) {
        return this.right(r);
    }

    @Deprecated
    default public LongIntPair second(Integer l) {
        return this.second((int)l);
    }

    default public int valueInt() {
        return this.rightInt();
    }

    @Override
    @Deprecated
    default public Integer value() {
        return this.valueInt();
    }

    default public LongIntPair value(int r) {
        return this.right(r);
    }

    @Deprecated
    default public LongIntPair value(Integer l) {
        return this.value((int)l);
    }

    public static LongIntPair of(long left, int right) {
        return new LongIntImmutablePair(left, right);
    }

    public static Comparator<LongIntPair> lexComparator() {
        return (x, y) -> {
            int t = Long.compare(x.leftLong(), y.leftLong());
            if (t != 0) {
                return t;
            }
            return Integer.compare(x.rightInt(), y.rightInt());
        };
    }
}

