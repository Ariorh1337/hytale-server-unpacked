/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleLongImmutablePair;
import java.util.Comparator;

public interface DoubleLongPair
extends Pair<Double, Long> {
    public double leftDouble();

    @Override
    @Deprecated
    default public Double left() {
        return this.leftDouble();
    }

    default public DoubleLongPair left(double l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleLongPair left(Double l) {
        return this.left((double)l);
    }

    default public double firstDouble() {
        return this.leftDouble();
    }

    @Override
    @Deprecated
    default public Double first() {
        return this.firstDouble();
    }

    default public DoubleLongPair first(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleLongPair first(Double l) {
        return this.first((double)l);
    }

    default public double keyDouble() {
        return this.firstDouble();
    }

    @Override
    @Deprecated
    default public Double key() {
        return this.keyDouble();
    }

    default public DoubleLongPair key(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleLongPair key(Double l) {
        return this.key((double)l);
    }

    public long rightLong();

    @Override
    @Deprecated
    default public Long right() {
        return this.rightLong();
    }

    default public DoubleLongPair right(long r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleLongPair right(Long l) {
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

    default public DoubleLongPair second(long r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleLongPair second(Long l) {
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

    default public DoubleLongPair value(long r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleLongPair value(Long l) {
        return this.value((long)l);
    }

    public static DoubleLongPair of(double left, long right) {
        return new DoubleLongImmutablePair(left, right);
    }

    public static Comparator<DoubleLongPair> lexComparator() {
        return (x, y) -> {
            int t = Double.compare(x.leftDouble(), y.leftDouble());
            if (t != 0) {
                return t;
            }
            return Long.compare(x.rightLong(), y.rightLong());
        };
    }
}

