/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntBytePair;
import java.io.Serializable;
import java.util.Objects;

public class IntByteMutablePair
implements IntBytePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected int left;
    protected byte right;

    public IntByteMutablePair(int left, byte right) {
        this.left = left;
        this.right = right;
    }

    public static IntByteMutablePair of(int left, byte right) {
        return new IntByteMutablePair(left, right);
    }

    @Override
    public int leftInt() {
        return this.left;
    }

    @Override
    public IntByteMutablePair left(int l) {
        this.left = l;
        return this;
    }

    @Override
    public byte rightByte() {
        return this.right;
    }

    @Override
    public IntByteMutablePair right(byte r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntBytePair) {
            return this.left == ((IntBytePair)other).leftInt() && this.right == ((IntBytePair)other).rightByte();
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
        return "<" + this.leftInt() + "," + this.rightByte() + ">";
    }
}

