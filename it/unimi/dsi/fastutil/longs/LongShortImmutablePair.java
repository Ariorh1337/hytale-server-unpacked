/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongShortPair;
import java.io.Serializable;
import java.util.Objects;

public class LongShortImmutablePair
implements LongShortPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final long left;
    protected final short right;

    public LongShortImmutablePair(long left, short right) {
        this.left = left;
        this.right = right;
    }

    public static LongShortImmutablePair of(long left, short right) {
        return new LongShortImmutablePair(left, right);
    }

    @Override
    public long leftLong() {
        return this.left;
    }

    @Override
    public short rightShort() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof LongShortPair) {
            return this.left == ((LongShortPair)other).leftLong() && this.right == ((LongShortPair)other).rightShort();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.long2int(this.left) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftLong() + "," + this.rightShort() + ">";
    }
}

