/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongShortPair;
import java.io.Serializable;
import java.util.Objects;

public class LongShortMutablePair
implements LongShortPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected long left;
    protected short right;

    public LongShortMutablePair(long left, short right) {
        this.left = left;
        this.right = right;
    }

    public static LongShortMutablePair of(long left, short right) {
        return new LongShortMutablePair(left, right);
    }

    @Override
    public long leftLong() {
        return this.left;
    }

    @Override
    public LongShortMutablePair left(long l) {
        this.left = l;
        return this;
    }

    @Override
    public short rightShort() {
        return this.right;
    }

    @Override
    public LongShortMutablePair right(short r) {
        this.right = r;
        return this;
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

