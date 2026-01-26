/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongFloatPair;
import java.io.Serializable;
import java.util.Objects;

public class LongFloatMutablePair
implements LongFloatPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected long left;
    protected float right;

    public LongFloatMutablePair(long left, float right) {
        this.left = left;
        this.right = right;
    }

    public static LongFloatMutablePair of(long left, float right) {
        return new LongFloatMutablePair(left, right);
    }

    @Override
    public long leftLong() {
        return this.left;
    }

    @Override
    public LongFloatMutablePair left(long l) {
        this.left = l;
        return this;
    }

    @Override
    public float rightFloat() {
        return this.right;
    }

    @Override
    public LongFloatMutablePair right(float r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof LongFloatPair) {
            return this.left == ((LongFloatPair)other).leftLong() && this.right == ((LongFloatPair)other).rightFloat();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(Float.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.long2int(this.left) * 19 + HashCommon.float2int(this.right);
    }

    public String toString() {
        return "<" + this.leftLong() + "," + this.rightFloat() + ">";
    }
}

