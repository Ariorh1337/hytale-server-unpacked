/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.ints.IntIntImmutableSortedPair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import java.io.Serializable;

public interface IntIntSortedPair
extends IntIntPair,
SortedPair<Integer>,
Serializable {
    public static IntIntSortedPair of(int left, int right) {
        return IntIntImmutableSortedPair.of(left, right);
    }

    default public boolean contains(int e) {
        return e == this.leftInt() || e == this.rightInt();
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.contains((Integer)o);
    }
}

