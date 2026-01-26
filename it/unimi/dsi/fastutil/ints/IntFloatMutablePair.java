/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntFloatPair;
import java.io.Serializable;
import java.util.Objects;

public class IntFloatMutablePair
implements IntFloatPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected int left;
    protected float right;

    public IntFloatMutablePair(int left, float right) {
        this.left = left;
        this.right = right;
    }

    public static IntFloatMutablePair of(int left, float right) {
        return new IntFloatMutablePair(left, right);
    }

    @Override
    public int leftInt() {
        return this.left;
    }

    @Override
    public IntFloatMutablePair left(int l) {
        this.left = l;
        return this;
    }

    @Override
    public float rightFloat() {
        return this.right;
    }

    @Override
    public IntFloatMutablePair right(float r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntFloatPair) {
            return this.left == ((IntFloatPair)other).leftInt() && this.right == ((IntFloatPair)other).rightFloat();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(Float.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + HashCommon.float2int(this.right);
    }

    public String toString() {
        return "<" + this.leftInt() + "," + this.rightFloat() + ">";
    }
}

