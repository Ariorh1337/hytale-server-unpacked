/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.tuple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BoolDoublePair
implements Comparable<BoolDoublePair> {
    private final boolean left;
    private final double right;

    public BoolDoublePair(boolean left, double right) {
        this.left = left;
        this.right = right;
    }

    public final boolean getKey() {
        return this.getLeft();
    }

    public boolean getLeft() {
        return this.left;
    }

    public final double getValue() {
        return this.getRight();
    }

    public double getRight() {
        return this.right;
    }

    @Override
    public int compareTo(@Nonnull BoolDoublePair other) {
        int compare = Boolean.compare(this.left, other.left);
        if (compare != 0) {
            return compare;
        }
        return Double.compare(this.right, other.right);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BoolDoublePair that = (BoolDoublePair)o;
        if (this.left != that.left) {
            return false;
        }
        return Double.compare(that.right, this.right) == 0;
    }

    public int hashCode() {
        int result = this.left ? 1 : 0;
        long temp = Double.doubleToLongBits(this.right);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }

    @Nonnull
    public String toString() {
        return "(" + this.getLeft() + "," + this.getRight() + ")";
    }

    @Nonnull
    public String toString(@Nonnull String format) {
        return String.format(format, this.getLeft(), this.getRight());
    }

    @Nonnull
    public static BoolDoublePair of(boolean left, double right) {
        return new BoolDoublePair(left, right);
    }
}

