/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleIntImmutablePair;
import java.util.Comparator;

public interface DoubleIntPair
extends Pair<Double, Integer> {
    public double leftDouble();

    @Override
    @Deprecated
    default public Double left() {
        return this.leftDouble();
    }

    default public DoubleIntPair left(double l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleIntPair left(Double l) {
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

    default public DoubleIntPair first(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleIntPair first(Double l) {
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

    default public DoubleIntPair key(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleIntPair key(Double l) {
        return this.key((double)l);
    }

    public int rightInt();

    @Override
    @Deprecated
    default public Integer right() {
        return this.rightInt();
    }

    default public DoubleIntPair right(int r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleIntPair right(Integer l) {
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

    default public DoubleIntPair second(int r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleIntPair second(Integer l) {
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

    default public DoubleIntPair value(int r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleIntPair value(Integer l) {
        return this.value((int)l);
    }

    public static DoubleIntPair of(double left, int right) {
        return new DoubleIntImmutablePair(left, right);
    }

    public static Comparator<DoubleIntPair> lexComparator() {
        return (x, y) -> {
            int t = Double.compare(x.leftDouble(), y.leftDouble());
            if (t != 0) {
                return t;
            }
            return Integer.compare(x.rightInt(), y.rightInt());
        };
    }
}

