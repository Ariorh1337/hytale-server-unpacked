/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleFloatImmutablePair;
import java.util.Comparator;

public interface DoubleFloatPair
extends Pair<Double, Float> {
    public double leftDouble();

    @Override
    @Deprecated
    default public Double left() {
        return this.leftDouble();
    }

    default public DoubleFloatPair left(double l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleFloatPair left(Double l) {
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

    default public DoubleFloatPair first(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleFloatPair first(Double l) {
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

    default public DoubleFloatPair key(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleFloatPair key(Double l) {
        return this.key((double)l);
    }

    public float rightFloat();

    @Override
    @Deprecated
    default public Float right() {
        return Float.valueOf(this.rightFloat());
    }

    default public DoubleFloatPair right(float r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleFloatPair right(Float l) {
        return this.right(l.floatValue());
    }

    default public float secondFloat() {
        return this.rightFloat();
    }

    @Override
    @Deprecated
    default public Float second() {
        return Float.valueOf(this.secondFloat());
    }

    default public DoubleFloatPair second(float r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleFloatPair second(Float l) {
        return this.second(l.floatValue());
    }

    default public float valueFloat() {
        return this.rightFloat();
    }

    @Override
    @Deprecated
    default public Float value() {
        return Float.valueOf(this.valueFloat());
    }

    default public DoubleFloatPair value(float r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleFloatPair value(Float l) {
        return this.value(l.floatValue());
    }

    public static DoubleFloatPair of(double left, float right) {
        return new DoubleFloatImmutablePair(left, right);
    }

    public static Comparator<DoubleFloatPair> lexComparator() {
        return (x, y) -> {
            int t = Double.compare(x.leftDouble(), y.leftDouble());
            if (t != 0) {
                return t;
            }
            return Float.compare(x.rightFloat(), y.rightFloat());
        };
    }
}

