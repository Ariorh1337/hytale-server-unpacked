/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanFloatPair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanFloatMutablePair
implements BooleanFloatPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected boolean left;
    protected float right;

    public BooleanFloatMutablePair(boolean left, float right) {
        this.left = left;
        this.right = right;
    }

    public static BooleanFloatMutablePair of(boolean left, float right) {
        return new BooleanFloatMutablePair(left, right);
    }

    @Override
    public boolean leftBoolean() {
        return this.left;
    }

    @Override
    public BooleanFloatMutablePair left(boolean l) {
        this.left = l;
        return this;
    }

    @Override
    public float rightFloat() {
        return this.right;
    }

    @Override
    public BooleanFloatMutablePair right(float r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof BooleanFloatPair) {
            return this.left == ((BooleanFloatPair)other).leftBoolean() && this.right == ((BooleanFloatPair)other).rightFloat();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(Float.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left ? 1231 : 1237) * 19 + HashCommon.float2int(this.right);
    }

    public String toString() {
        return "<" + this.leftBoolean() + "," + this.rightFloat() + ">";
    }
}

