/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntDoublePair;
import java.io.Serializable;
import java.util.Objects;

public class IntDoubleMutablePair
implements IntDoublePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected int left;
    protected double right;

    public IntDoubleMutablePair(int left, double right) {
        this.left = left;
        this.right = right;
    }

    public static IntDoubleMutablePair of(int left, double right) {
        return new IntDoubleMutablePair(left, right);
    }

    @Override
    public int leftInt() {
        return this.left;
    }

    @Override
    public IntDoubleMutablePair left(int l) {
        this.left = l;
        return this;
    }

    @Override
    public double rightDouble() {
        return this.right;
    }

    @Override
    public IntDoubleMutablePair right(double r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntDoublePair) {
            return this.left == ((IntDoublePair)other).leftInt() && this.right == ((IntDoublePair)other).rightDouble();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + HashCommon.double2int(this.right);
    }

    public String toString() {
        return "<" + this.leftInt() + "," + this.rightDouble() + ">";
    }
}

