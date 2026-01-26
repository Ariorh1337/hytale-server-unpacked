/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongComparators;
import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface LongComparator
extends Comparator<Long> {
    @Override
    public int compare(long var1, long var3);

    default public LongComparator reversed() {
        return LongComparators.oppositeComparator(this);
    }

    @Override
    @Deprecated
    default public int compare(Long ok1, Long ok2) {
        return this.compare((long)ok1, (long)ok2);
    }

    default public LongComparator thenComparing(LongComparator second) {
        return (LongComparator & Serializable)(k1, k2) -> {
            int comp = this.compare(k1, k2);
            return comp == 0 ? second.compare(k1, k2) : comp;
        };
    }

    @Override
    default public Comparator<Long> thenComparing(Comparator<? super Long> second) {
        if (second instanceof LongComparator) {
            return this.thenComparing((LongComparator)second);
        }
        return Comparator.super.thenComparing(second);
    }
}

