/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleShortPair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleShortMutablePair
implements DoubleShortPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected double left;
    protected short right;

    public DoubleShortMutablePair(double left, short right) {
        this.left = left;
        this.right = right;
    }

    public static DoubleShortMutablePair of(double left, short right) {
        return new DoubleShortMutablePair(left, right);
    }

    @Override
    public double leftDouble() {
        return this.left;
    }

    @Override
    public DoubleShortMutablePair left(double l) {
        this.left = l;
        return this;
    }

    @Override
    public short rightShort() {
        return this.right;
    }

    @Override
    public DoubleShortMutablePair right(short r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof DoubleShortPair) {
            return this.left == ((DoubleShortPair)other).leftDouble() && this.right == ((DoubleShortPair)other).rightShort();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.double2int(this.left) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftDouble() + "," + this.rightShort() + ">";
    }
}

