/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortComparators;
import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface ShortComparator
extends Comparator<Short> {
    @Override
    public int compare(short var1, short var2);

    default public ShortComparator reversed() {
        return ShortComparators.oppositeComparator(this);
    }

    @Override
    @Deprecated
    default public int compare(Short ok1, Short ok2) {
        return this.compare((short)ok1, (short)ok2);
    }

    default public ShortComparator thenComparing(ShortComparator second) {
        return (ShortComparator & Serializable)(k1, k2) -> {
            int comp = this.compare(k1, k2);
            return comp == 0 ? second.compare(k1, k2) : comp;
        };
    }

    @Override
    default public Comparator<Short> thenComparing(Comparator<? super Short> second) {
        if (second instanceof ShortComparator) {
            return this.thenComparing((ShortComparator)second);
        }
        return Comparator.super.thenComparing(second);
    }
}

