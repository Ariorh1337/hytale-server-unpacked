/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutableSortedPair;
import it.unimi.dsi.fastutil.doubles.DoubleDoublePair;
import java.io.Serializable;

public interface DoubleDoubleSortedPair
extends DoubleDoublePair,
SortedPair<Double>,
Serializable {
    public static DoubleDoubleSortedPair of(double left, double right) {
        return DoubleDoubleImmutableSortedPair.of(left, right);
    }

    default public boolean contains(double e) {
        return e == this.leftDouble() || e == this.rightDouble();
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.contains((Double)o);
    }
}

