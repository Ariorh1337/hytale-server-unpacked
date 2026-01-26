/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntShortPair;
import java.io.Serializable;
import java.util.Objects;

public class IntShortMutablePair
implements IntShortPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected int left;
    protected short right;

    public IntShortMutablePair(int left, short right) {
        this.left = left;
        this.right = right;
    }

    public static IntShortMutablePair of(int left, short right) {
        return new IntShortMutablePair(left, right);
    }

    @Override
    public int leftInt() {
        return this.left;
    }

    @Override
    public IntShortMutablePair left(int l) {
        this.left = l;
        return this;
    }

    @Override
    public short rightShort() {
        return this.right;
    }

    @Override
    public IntShortMutablePair right(short r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntShortPair) {
            return this.left == ((IntShortPair)other).leftInt() && this.right == ((IntShortPair)other).rightShort();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftInt() + "," + this.rightShort() + ">";
    }
}

