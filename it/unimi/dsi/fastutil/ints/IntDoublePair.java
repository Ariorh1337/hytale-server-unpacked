/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntDoubleImmutablePair;
import java.util.Comparator;

public interface IntDoublePair
extends Pair<Integer, Double> {
    public int leftInt();

    @Override
    @Deprecated
    default public Integer left() {
        return this.leftInt();
    }

    default public IntDoublePair left(int l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntDoublePair left(Integer l) {
        return this.left((int)l);
    }

    default public int firstInt() {
        return this.leftInt();
    }

    @Override
    @Deprecated
    default public Integer first() {
        return this.firstInt();
    }

    default public IntDoublePair first(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntDoublePair first(Integer l) {
        return this.first((int)l);
    }

    default public int keyInt() {
        return this.firstInt();
    }

    @Override
    @Deprecated
    default public Integer key() {
        return this.keyInt();
    }

    default public IntDoublePair key(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntDoublePair key(Integer l) {
        return this.key((int)l);
    }

    public double rightDouble();

    @Override
    @Deprecated
    default public Double right() {
        return this.rightDouble();
    }

    default public IntDoublePair right(double r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntDoublePair right(Double l) {
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

    default public IntDoublePair second(double r) {
        return this.right(r);
    }

    @Deprecated
    default public IntDoublePair second(Double l) {
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

    default public IntDoublePair value(double r) {
        return this.right(r);
    }

    @Deprecated
    default public IntDoublePair value(Double l) {
        return this.value((double)l);
    }

    public static IntDoublePair of(int left, double right) {
        return new IntDoubleImmutablePair(left, right);
    }

    public static Comparator<IntDoublePair> lexComparator() {
        return (x, y) -> {
            int t = Integer.compare(x.leftInt(), y.leftInt());
            if (t != 0) {
                return t;
            }
            return Double.compare(x.rightDouble(), y.rightDouble());
        };
    }
}

