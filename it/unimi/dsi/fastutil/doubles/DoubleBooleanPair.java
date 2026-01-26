/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleBooleanImmutablePair;
import java.util.Comparator;

public interface DoubleBooleanPair
extends Pair<Double, Boolean> {
    public double leftDouble();

    @Override
    @Deprecated
    default public Double left() {
        return this.leftDouble();
    }

    default public DoubleBooleanPair left(double l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleBooleanPair left(Double l) {
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

    default public DoubleBooleanPair first(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleBooleanPair first(Double l) {
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

    default public DoubleBooleanPair key(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleBooleanPair key(Double l) {
        return this.key((double)l);
    }

    public boolean rightBoolean();

    @Override
    @Deprecated
    default public Boolean right() {
        return this.rightBoolean();
    }

    default public DoubleBooleanPair right(boolean r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleBooleanPair right(Boolean l) {
        return this.right((boolean)l);
    }

    default public boolean secondBoolean() {
        return this.rightBoolean();
    }

    @Override
    @Deprecated
    default public Boolean second() {
        return this.secondBoolean();
    }

    default public DoubleBooleanPair second(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleBooleanPair second(Boolean l) {
        return this.second((boolean)l);
    }

    default public boolean valueBoolean() {
        return this.rightBoolean();
    }

    @Override
    @Deprecated
    default public Boolean value() {
        return this.valueBoolean();
    }

    default public DoubleBooleanPair value(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleBooleanPair value(Boolean l) {
        return this.value((boolean)l);
    }

    public static DoubleBooleanPair of(double left, boolean right) {
        return new DoubleBooleanImmutablePair(left, right);
    }

    public static Comparator<DoubleBooleanPair> lexComparator() {
        return (x, y) -> {
            int t = Double.compare(x.leftDouble(), y.leftDouble());
            if (t != 0) {
                return t;
            }
            return Boolean.compare(x.rightBoolean(), y.rightBoolean());
        };
    }
}

