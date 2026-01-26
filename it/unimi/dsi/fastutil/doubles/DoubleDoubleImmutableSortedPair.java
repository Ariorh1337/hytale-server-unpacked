/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleSortedPair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleDoubleImmutableSortedPair
extends DoubleDoubleImmutablePair
implements DoubleDoubleSortedPair,
Serializable {
    private static final long serialVersionUID = 0L;

    private DoubleDoubleImmutableSortedPair(double left, double right) {
        super(left, right);
    }

    public static DoubleDoubleImmutableSortedPair of(double left, double right) {
        if (left <= right) {
            return new DoubleDoubleImmutableSortedPair(left, right);
        }
        return new DoubleDoubleImmutableSortedPair(right, left);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof DoubleDoubleSortedPair) {
            return this.left == ((DoubleDoubleSortedPair)other).leftDouble() && this.right == ((DoubleDoubleSortedPair)other).rightDouble();
        }
        if (other instanceof SortedPair) {
            return Objects.equals(this.left, ((SortedPair)other).left()) && Objects.equals(this.right, ((SortedPair)other).right());
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + this.leftDouble() + "," + this.rightDouble() + "}";
    }
}

