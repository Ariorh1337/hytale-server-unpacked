/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanDoubleImmutablePair;
import java.util.Comparator;

public interface BooleanDoublePair
extends Pair<Boolean, Double> {
    public boolean leftBoolean();

    @Override
    @Deprecated
    default public Boolean left() {
        return this.leftBoolean();
    }

    default public BooleanDoublePair left(boolean l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanDoublePair left(Boolean l) {
        return this.left((boolean)l);
    }

    default public boolean firstBoolean() {
        return this.leftBoolean();
    }

    @Override
    @Deprecated
    default public Boolean first() {
        return this.firstBoolean();
    }

    default public BooleanDoublePair first(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanDoublePair first(Boolean l) {
        return this.first((boolean)l);
    }

    default public boolean keyBoolean() {
        return this.firstBoolean();
    }

    @Override
    @Deprecated
    default public Boolean key() {
        return this.keyBoolean();
    }

    default public BooleanDoublePair key(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanDoublePair key(Boolean l) {
        return this.key((boolean)l);
    }

    public double rightDouble();

    @Override
    @Deprecated
    default public Double right() {
        return this.rightDouble();
    }

    default public BooleanDoublePair right(double r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanDoublePair right(Double l) {
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

    default public BooleanDoublePair second(double r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanDoublePair second(Double l) {
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

    default public BooleanDoublePair value(double r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanDoublePair value(Double l) {
        return this.value((double)l);
    }

    public static BooleanDoublePair of(boolean left, double right) {
        return new BooleanDoubleImmutablePair(left, right);
    }

    public static Comparator<BooleanDoublePair> lexComparator() {
        return (x, y) -> {
            int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
            if (t != 0) {
                return t;
            }
            return Double.compare(x.rightDouble(), y.rightDouble());
        };
    }
}

