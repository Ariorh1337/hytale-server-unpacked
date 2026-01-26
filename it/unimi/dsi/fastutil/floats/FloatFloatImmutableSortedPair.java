/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.floats.FloatFloatImmutablePair;
import it.unimi.dsi.fastutil.floats.FloatFloatSortedPair;
import java.io.Serializable;
import java.util.Objects;

public class FloatFloatImmutableSortedPair
extends FloatFloatImmutablePair
implements FloatFloatSortedPair,
Serializable {
    private static final long serialVersionUID = 0L;

    private FloatFloatImmutableSortedPair(float left, float right) {
        super(left, right);
    }

    public static FloatFloatImmutableSortedPair of(float left, float right) {
        if (left <= right) {
            return new FloatFloatImmutableSortedPair(left, right);
        }
        return new FloatFloatImmutableSortedPair(right, left);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof FloatFloatSortedPair) {
            return this.left == ((FloatFloatSortedPair)other).leftFloat() && this.right == ((FloatFloatSortedPair)other).rightFloat();
        }
        if (other instanceof SortedPair) {
            return Objects.equals(Float.valueOf(this.left), ((SortedPair)other).left()) && Objects.equals(Float.valueOf(this.right), ((SortedPair)other).right());
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + this.leftFloat() + "," + this.rightFloat() + "}";
    }
}

