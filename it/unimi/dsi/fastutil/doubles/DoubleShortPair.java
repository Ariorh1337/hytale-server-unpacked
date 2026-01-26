/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleShortImmutablePair;
import java.util.Comparator;

public interface DoubleShortPair
extends Pair<Double, Short> {
    public double leftDouble();

    @Override
    @Deprecated
    default public Double left() {
        return this.leftDouble();
    }

    default public DoubleShortPair left(double l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleShortPair left(Double l) {
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

    default public DoubleShortPair first(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleShortPair first(Double l) {
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

    default public DoubleShortPair key(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleShortPair key(Double l) {
        return this.key((double)l);
    }

    public short rightShort();

    @Override
    @Deprecated
    default public Short right() {
        return this.rightShort();
    }

    default public DoubleShortPair right(short r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleShortPair right(Short l) {
        return this.right((short)l);
    }

    default public short secondShort() {
        return this.rightShort();
    }

    @Override
    @Deprecated
    default public Short second() {
        return this.secondShort();
    }

    default public DoubleShortPair second(short r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleShortPair second(Short l) {
        return this.second((short)l);
    }

    default public short valueShort() {
        return this.rightShort();
    }

    @Override
    @Deprecated
    default public Short value() {
        return this.valueShort();
    }

    default public DoubleShortPair value(short r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleShortPair value(Short l) {
        return this.value((short)l);
    }

    public static DoubleShortPair of(double left, short right) {
        return new DoubleShortImmutablePair(left, right);
    }

    public static Comparator<DoubleShortPair> lexComparator() {
        return (x, y) -> {
            int t = Double.compare(x.leftDouble(), y.leftDouble());
            if (t != 0) {
                return t;
            }
            return Short.compare(x.rightShort(), y.rightShort());
        };
    }
}

