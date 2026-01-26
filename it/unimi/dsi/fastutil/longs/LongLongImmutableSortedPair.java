/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;
import it.unimi.dsi.fastutil.longs.LongLongSortedPair;
import java.io.Serializable;
import java.util.Objects;

public class LongLongImmutableSortedPair
extends LongLongImmutablePair
implements LongLongSortedPair,
Serializable {
    private static final long serialVersionUID = 0L;

    private LongLongImmutableSortedPair(long left, long right) {
        super(left, right);
    }

    public static LongLongImmutableSortedPair of(long left, long right) {
        if (left <= right) {
            return new LongLongImmutableSortedPair(left, right);
        }
        return new LongLongImmutableSortedPair(right, left);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof LongLongSortedPair) {
            return this.left == ((LongLongSortedPair)other).leftLong() && this.right == ((LongLongSortedPair)other).rightLong();
        }
        if (other instanceof SortedPair) {
            return Objects.equals(this.left, ((SortedPair)other).left()) && Objects.equals(this.right, ((SortedPair)other).right());
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + this.leftLong() + "," + this.rightLong() + "}";
    }
}

