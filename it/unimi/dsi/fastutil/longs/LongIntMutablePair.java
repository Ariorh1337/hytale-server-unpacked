/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongIntPair;
import java.io.Serializable;
import java.util.Objects;

public class LongIntMutablePair
implements LongIntPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected long left;
    protected int right;

    public LongIntMutablePair(long left, int right) {
        this.left = left;
        this.right = right;
    }

    public static LongIntMutablePair of(long left, int right) {
        return new LongIntMutablePair(left, right);
    }

    @Override
    public long leftLong() {
        return this.left;
    }

    @Override
    public LongIntMutablePair left(long l) {
        this.left = l;
        return this;
    }

    @Override
    public int rightInt() {
        return this.right;
    }

    @Override
    public LongIntMutablePair right(int r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof LongIntPair) {
            return this.left == ((LongIntPair)other).leftLong() && this.right == ((LongIntPair)other).rightInt();
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
        return "<" + this.leftLong() + "," + this.rightInt() + ">";
    }
}

