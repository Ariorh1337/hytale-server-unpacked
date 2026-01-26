/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntLongPair;
import java.io.Serializable;
import java.util.Objects;

public class IntLongMutablePair
implements IntLongPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected int left;
    protected long right;

    public IntLongMutablePair(int left, long right) {
        this.left = left;
        this.right = right;
    }

    public static IntLongMutablePair of(int left, long right) {
        return new IntLongMutablePair(left, right);
    }

    @Override
    public int leftInt() {
        return this.left;
    }

    @Override
    public IntLongMutablePair left(int l) {
        this.left = l;
        return this;
    }

    @Override
    public long rightLong() {
        return this.right;
    }

    @Override
    public IntLongMutablePair right(long r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntLongPair) {
            return this.left == ((IntLongPair)other).leftInt() && this.right == ((IntLongPair)other).rightLong();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + HashCommon.long2int(this.right);
    }

    public String toString() {
        return "<" + this.leftInt() + "," + this.rightLong() + ">";
    }
}

