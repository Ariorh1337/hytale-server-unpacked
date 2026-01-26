/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleCharPair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleCharMutablePair
implements DoubleCharPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected double left;
    protected char right;

    public DoubleCharMutablePair(double left, char right) {
        this.left = left;
        this.right = right;
    }

    public static DoubleCharMutablePair of(double left, char right) {
        return new DoubleCharMutablePair(left, right);
    }

    @Override
    public double leftDouble() {
        return this.left;
    }

    @Override
    public DoubleCharMutablePair left(double l) {
        this.left = l;
        return this;
    }

    @Override
    public char rightChar() {
        return this.right;
    }

    @Override
    public DoubleCharMutablePair right(char r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof DoubleCharPair) {
            return this.left == ((DoubleCharPair)other).leftDouble() && this.right == ((DoubleCharPair)other).rightChar();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(Character.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.double2int(this.left) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftDouble() + "," + this.rightChar() + ">";
    }
}

