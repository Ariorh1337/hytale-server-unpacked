/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleComparators;
import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface DoubleComparator
extends Comparator<Double> {
    @Override
    public int compare(double var1, double var3);

    default public DoubleComparator reversed() {
        return DoubleComparators.oppositeComparator(this);
    }

    @Override
    @Deprecated
    default public int compare(Double ok1, Double ok2) {
        return this.compare((double)ok1, (double)ok2);
    }

    default public DoubleComparator thenComparing(DoubleComparator second) {
        return (DoubleComparator & Serializable)(k1, k2) -> {
            int comp = this.compare(k1, k2);
            return comp == 0 ? second.compare(k1, k2) : comp;
        };
    }

    @Override
    default public Comparator<Double> thenComparing(Comparator<? super Double> second) {
        if (second instanceof DoubleComparator) {
            return this.thenComparing((DoubleComparator)second);
        }
        return Comparator.super.thenComparing(second);
    }
}

