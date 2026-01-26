/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanCharPair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanCharMutablePair
implements BooleanCharPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected boolean left;
    protected char right;

    public BooleanCharMutablePair(boolean left, char right) {
        this.left = left;
        this.right = right;
    }

    public static BooleanCharMutablePair of(boolean left, char right) {
        return new BooleanCharMutablePair(left, right);
    }

    @Override
    public boolean leftBoolean() {
        return this.left;
    }

    @Override
    public BooleanCharMutablePair left(boolean l) {
        this.left = l;
        return this;
    }

    @Override
    public char rightChar() {
        return this.right;
    }

    @Override
    public BooleanCharMutablePair right(char r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof BooleanCharPair) {
            return this.left == ((BooleanCharPair)other).leftBoolean() && this.right == ((BooleanCharPair)other).rightChar();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(Character.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left ? 1231 : 1237) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftBoolean() + "," + this.rightChar() + ">";
    }
}

