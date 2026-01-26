/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongDoubleImmutablePair;
import java.util.Comparator;

public interface LongDoublePair
extends Pair<Long, Double> {
    public long leftLong();

    @Override
    @Deprecated
    default public Long left() {
        return this.leftLong();
    }

    default public LongDoublePair left(long l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongDoublePair left(Long l) {
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

    default public LongDoublePair first(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongDoublePair first(Long l) {
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

    default public LongDoublePair key(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongDoublePair key(Long l) {
        return this.key((long)l);
    }

    public double rightDouble();

    @Override
    @Deprecated
    default public Double right() {
        return this.rightDouble();
    }

    default public LongDoublePair right(double r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongDoublePair right(Double l) {
        return this.right((double)l);
    }

    default public double secondDouble() {
        return this.rightDouble();
    }

    @Override
    @Deprecated
    default public Double second() {
        return this.secondDouble();
    }

    default public LongDoublePair second(double r) {
        return this.right(r);
    }

    @Deprecated
    default public LongDoublePair second(Double l) {
        return this.second((double)l);
    }

    default public double valueDouble() {
        return this.rightDouble();
    }

    @Override
    @Deprecated
    default public Double value() {
        return this.valueDouble();
    }

    default public LongDoublePair value(double r) {
        return this.right(r);
    }

    @Deprecated
    default public LongDoublePair value(Double l) {
        return this.value((double)l);
    }

    public static LongDoublePair of(long left, double right) {
        return new LongDoubleImmutablePair(left, right);
    }

    public static Comparator<LongDoublePair> lexComparator() {
        return (x, y) -> {
            int t = Long.compare(x.leftLong(), y.leftLong());
            if (t != 0) {
                return t;
            }
            return Double.compare(x.rightDouble(), y.rightDouble());
        };
    }
}

