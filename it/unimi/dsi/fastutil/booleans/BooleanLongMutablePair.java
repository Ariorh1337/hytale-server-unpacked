/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanLongPair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanLongMutablePair
implements BooleanLongPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected boolean left;
    protected long right;

    public BooleanLongMutablePair(boolean left, long right) {
        this.left = left;
        this.right = right;
    }

    public static BooleanLongMutablePair of(boolean left, long right) {
        return new BooleanLongMutablePair(left, right);
    }

    @Override
    public boolean leftBoolean() {
        return this.left;
    }

    @Override
    public BooleanLongMutablePair left(boolean l) {
        this.left = l;
        return this;
    }

    @Override
    public long rightLong() {
        return this.right;
    }

    @Override
    public BooleanLongMutablePair right(long r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof BooleanLongPair) {
            return this.left == ((BooleanLongPair)other).leftBoolean() && this.right == ((BooleanLongPair)other).rightLong();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left ? 1231 : 1237) * 19 + HashCommon.long2int(this.right);
    }

    public String toString() {
        return "<" + this.leftBoolean() + "," + this.rightLong() + ">";
    }
}

