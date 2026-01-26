/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortDoubleImmutablePair;
import java.util.Comparator;

public interface ShortDoublePair
extends Pair<Short, Double> {
    public short leftShort();

    @Override
    @Deprecated
    default public Short left() {
        return this.leftShort();
    }

    default public ShortDoublePair left(short l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortDoublePair left(Short l) {
        return this.left((short)l);
    }

    default public short firstShort() {
        return this.leftShort();
    }

    @Override
    @Deprecated
    default public Short first() {
        return this.firstShort();
    }

    default public ShortDoublePair first(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortDoublePair first(Short l) {
        return this.first((short)l);
    }

    default public short keyShort() {
        return this.firstShort();
    }

    @Override
    @Deprecated
    default public Short key() {
        return this.keyShort();
    }

    default public ShortDoublePair key(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortDoublePair key(Short l) {
        return this.key((short)l);
    }

    public double rightDouble();

    @Override
    @Deprecated
    default public Double right() {
        return this.rightDouble();
    }

    default public ShortDoublePair right(double r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortDoublePair right(Double l) {
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

    default public ShortDoublePair second(double r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortDoublePair second(Double l) {
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

    default public ShortDoublePair value(double r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortDoublePair value(Double l) {
        return this.value((double)l);
    }

    public static ShortDoublePair of(short left, double right) {
        return new ShortDoubleImmutablePair(left, right);
    }

    public static Comparator<ShortDoublePair> lexComparator() {
        return (x, y) -> {
            int t = Short.compare(x.leftShort(), y.leftShort());
            if (t != 0) {
                return t;
            }
            return Double.compare(x.rightDouble(), y.rightDouble());
        };
    }
}

