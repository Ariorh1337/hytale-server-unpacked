/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntCharPair;
import java.io.Serializable;
import java.util.Objects;

public class IntCharMutablePair
implements IntCharPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected int left;
    protected char right;

    public IntCharMutablePair(int left, char right) {
        this.left = left;
        this.right = right;
    }

    public static IntCharMutablePair of(int left, char right) {
        return new IntCharMutablePair(left, right);
    }

    @Override
    public int leftInt() {
        return this.left;
    }

    @Override
    public IntCharMutablePair left(int l) {
        this.left = l;
        return this;
    }

    @Override
    public char rightChar() {
        return this.right;
    }

    @Override
    public IntCharMutablePair right(char r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntCharPair) {
            return this.left == ((IntCharPair)other).leftInt() && this.right == ((IntCharPair)other).rightChar();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(Character.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftInt() + "," + this.rightChar() + ">";
    }
}

