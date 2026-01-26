/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleObjectImmutablePair;
import java.util.Comparator;

public interface DoubleObjectPair<V>
extends Pair<Double, V> {
    public double leftDouble();

    @Override
    @Deprecated
    default public Double left() {
        return this.leftDouble();
    }

    default public DoubleObjectPair<V> left(double l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleObjectPair<V> left(Double l) {
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

    default public DoubleObjectPair<V> first(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleObjectPair<V> first(Double l) {
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

    default public DoubleObjectPair<V> key(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleObjectPair<V> key(Double l) {
        return this.key((double)l);
    }

    public static <V> DoubleObjectPair<V> of(double left, V right) {
        return new DoubleObjectImmutablePair<V>(left, right);
    }

    public static <V> Comparator<DoubleObjectPair<V>> lexComparator() {
        return (x, y) -> {
            int t = Double.compare(x.leftDouble(), y.leftDouble());
            if (t != 0) {
                return t;
            }
            return ((Comparable)x.right()).compareTo(y.right());
        };
    }
}

