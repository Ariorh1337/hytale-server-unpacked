/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.floats.FloatFloatImmutableSortedPair;
import it.unimi.dsi.fastutil.floats.FloatFloatPair;
import java.io.Serializable;

public interface FloatFloatSortedPair
extends FloatFloatPair,
SortedPair<Float>,
Serializable {
    public static FloatFloatSortedPair of(float left, float right) {
        return FloatFloatImmutableSortedPair.of(left, right);
    }

    default public boolean contains(float e) {
        return e == this.leftFloat() || e == this.rightFloat();
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.contains(((Float)o).floatValue());
    }
}

