/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanDoublePair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanDoubleMutablePair
implements BooleanDoublePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected boolean left;
    protected double right;

    public BooleanDoubleMutablePair(boolean left, double right) {
        this.left = left;
        this.right = right;
    }

    public static BooleanDoubleMutablePair of(boolean left, double right) {
        return new BooleanDoubleMutablePair(left, right);
    }

    @Override
    public boolean leftBoolean() {
        return this.left;
    }

    @Override
    public BooleanDoubleMutablePair left(boolean l) {
        this.left = l;
        return this;
    }

    @Override
    public double rightDouble() {
        return this.right;
    }

    @Override
    public BooleanDoubleMutablePair right(double r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof BooleanDoublePair) {
            return this.left == ((BooleanDoublePair)other).leftBoolean() && this.right == ((BooleanDoublePair)other).rightDouble();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left ? 1231 : 1237) * 19 + HashCommon.double2int(this.right);
    }

    public String toString() {
        return "<" + this.leftBoolean() + "," + this.rightDouble() + ">";
    }
}

