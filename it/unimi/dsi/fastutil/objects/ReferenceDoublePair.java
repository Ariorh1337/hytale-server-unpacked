/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceDoubleImmutablePair;

public interface ReferenceDoublePair<K>
extends Pair<K, Double> {
    public double rightDouble();

    @Override
    @Deprecated
    default public Double right() {
        return this.rightDouble();
    }

    default public ReferenceDoublePair<K> right(double r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ReferenceDoublePair<K> right(Double l) {
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

    default public ReferenceDoublePair<K> second(double r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceDoublePair<K> second(Double l) {
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

    default public ReferenceDoublePair<K> value(double r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceDoublePair<K> value(Double l) {
        return this.value((double)l);
    }

    public static <K> ReferenceDoublePair<K> of(K left, double right) {
        return new ReferenceDoubleImmutablePair<K>(left, right);
    }
}

