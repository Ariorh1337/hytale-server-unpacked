/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import java.util.Comparator;

public interface DoubleDoublePair
extends Pair<Double, Double> {
    public double leftDouble();

    @Override
    @Deprecated
    default public Double left() {
        return this.leftDouble();
    }

    default public DoubleDoublePair left(double l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleDoublePair left(Double l) {
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

    default public DoubleDoublePair first(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleDoublePair first(Double l) {
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

    default public DoubleDoublePair key(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleDoublePair key(Double l) {
        return this.key((double)l);
    }

    public double rightDouble();

    @Override
    @Deprecated
    default public Double right() {
        return this.rightDouble();
    }

    default public DoubleDoublePair right(double r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleDoublePair right(Double l) {
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

    default public DoubleDoublePair second(double r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleDoublePair second(Double l) {
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

    default public DoubleDoublePair value(double r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleDoublePair value(Double l) {
        return this.value((double)l);
    }

    public static DoubleDoublePair of(double left, double right) {
        return new DoubleDoubleImmutablePair(left, right);
    }

    public static Comparator<DoubleDoublePair> lexComparator() {
        return (x, y) -> {
            int t = Double.compare(x.leftDouble(), y.leftDouble());
            if (t != 0) {
                return t;
            }
            return Double.compare(x.rightDouble(), y.rightDouble());
        };
    }
}

