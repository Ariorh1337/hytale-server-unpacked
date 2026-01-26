/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanComparators;
import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface BooleanComparator
extends Comparator<Boolean> {
    @Override
    public int compare(boolean var1, boolean var2);

    default public BooleanComparator reversed() {
        return BooleanComparators.oppositeComparator(this);
    }

    @Override
    @Deprecated
    default public int compare(Boolean ok1, Boolean ok2) {
        return this.compare((boolean)ok1, (boolean)ok2);
    }

    default public BooleanComparator thenComparing(BooleanComparator second) {
        return (BooleanComparator & Serializable)(k1, k2) -> {
            int comp = this.compare(k1, k2);
            return comp == 0 ? second.compare(k1, k2) : comp;
        };
    }

    @Override
    default public Comparator<Boolean> thenComparing(Comparator<? super Boolean> second) {
        if (second instanceof BooleanComparator) {
            return this.thenComparing((BooleanComparator)second);
        }
        return Comparator.super.thenComparing(second);
    }
}

