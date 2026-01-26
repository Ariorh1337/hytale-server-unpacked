/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleReferenceImmutablePair;

public interface DoubleReferencePair<V>
extends Pair<Double, V> {
    public double leftDouble();

    @Override
    @Deprecated
    default public Double left() {
        return this.leftDouble();
    }

    default public DoubleReferencePair<V> left(double l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleReferencePair<V> left(Double l) {
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

    default public DoubleReferencePair<V> first(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleReferencePair<V> first(Double l) {
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

    default public DoubleReferencePair<V> key(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleReferencePair<V> key(Double l) {
        return this.key((double)l);
    }

    public static <V> DoubleReferencePair<V> of(double left, V right) {
        return new DoubleReferenceImmutablePair<V>(left, right);
    }
}

