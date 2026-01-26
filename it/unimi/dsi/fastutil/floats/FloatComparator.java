/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatComparators;
import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface FloatComparator
extends Comparator<Float> {
    @Override
    public int compare(float var1, float var2);

    default public FloatComparator reversed() {
        return FloatComparators.oppositeComparator(this);
    }

    @Override
    @Deprecated
    default public int compare(Float ok1, Float ok2) {
        return this.compare(ok1.floatValue(), ok2.floatValue());
    }

    default public FloatComparator thenComparing(FloatComparator second) {
        return (FloatComparator & Serializable)(k1, k2) -> {
            int comp = this.compare(k1, k2);
            return comp == 0 ? second.compare(k1, k2) : comp;
        };
    }

    @Override
    default public Comparator<Float> thenComparing(Comparator<? super Float> second) {
        if (second instanceof FloatComparator) {
            return this.thenComparing((FloatComparator)second);
        }
        return Comparator.super.thenComparing(second);
    }
}

