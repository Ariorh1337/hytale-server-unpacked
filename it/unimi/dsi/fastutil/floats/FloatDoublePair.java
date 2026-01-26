/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatDoubleImmutablePair;
import java.util.Comparator;

public interface FloatDoublePair
extends Pair<Float, Double> {
    public float leftFloat();

    @Override
    @Deprecated
    default public Float left() {
        return Float.valueOf(this.leftFloat());
    }

    default public FloatDoublePair left(float l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatDoublePair left(Float l) {
        return this.left(l.floatValue());
    }

    default public float firstFloat() {
        return this.leftFloat();
    }

    @Override
    @Deprecated
    default public Float first() {
        return Float.valueOf(this.firstFloat());
    }

    default public FloatDoublePair first(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatDoublePair first(Float l) {
        return this.first(l.floatValue());
    }

    default public float keyFloat() {
        return this.firstFloat();
    }

    @Override
    @Deprecated
    default public Float key() {
        return Float.valueOf(this.keyFloat());
    }

    default public FloatDoublePair key(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatDoublePair key(Float l) {
        return this.key(l.floatValue());
    }

    public double rightDouble();

    @Override
    @Deprecated
    default public Double right() {
        return this.rightDouble();
    }

    default public FloatDoublePair right(double r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatDoublePair right(Double l) {
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

    default public FloatDoublePair second(double r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatDoublePair second(Double l) {
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

    default public FloatDoublePair value(double r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatDoublePair value(Double l) {
        return this.value((double)l);
    }

    public static FloatDoublePair of(float left, double right) {
        return new FloatDoubleImmutablePair(left, right);
    }

    public static Comparator<FloatDoublePair> lexComparator() {
        return (x, y) -> {
            int t = Float.compare(x.leftFloat(), y.leftFloat());
            if (t != 0) {
                return t;
            }
            return Double.compare(x.rightDouble(), y.rightDouble());
        };
    }
}

