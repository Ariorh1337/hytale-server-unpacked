/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanIntPair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanIntMutablePair
implements BooleanIntPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected boolean left;
    protected int right;

    public BooleanIntMutablePair(boolean left, int right) {
        this.left = left;
        this.right = right;
    }

    public static BooleanIntMutablePair of(boolean left, int right) {
        return new BooleanIntMutablePair(left, right);
    }

    @Override
    public boolean leftBoolean() {
        return this.left;
    }

    @Override
    public BooleanIntMutablePair left(boolean l) {
        this.left = l;
        return this;
    }

    @Override
    public int rightInt() {
        return this.right;
    }

    @Override
    public BooleanIntMutablePair right(int r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof BooleanIntPair) {
            return this.left == ((BooleanIntPair)other).leftBoolean() && this.right == ((BooleanIntPair)other).rightInt();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left ? 1231 : 1237) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftBoolean() + "," + this.rightInt() + ">";
    }
}

