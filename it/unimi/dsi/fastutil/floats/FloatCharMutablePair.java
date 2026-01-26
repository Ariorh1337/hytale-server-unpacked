/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatCharPair;
import java.io.Serializable;
import java.util.Objects;

public class FloatCharMutablePair
implements FloatCharPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected float left;
    protected char right;

    public FloatCharMutablePair(float left, char right) {
        this.left = left;
        this.right = right;
    }

    public static FloatCharMutablePair of(float left, char right) {
        return new FloatCharMutablePair(left, right);
    }

    @Override
    public float leftFloat() {
        return this.left;
    }

    @Override
    public FloatCharMutablePair left(float l) {
        this.left = l;
        return this;
    }

    @Override
    public char rightChar() {
        return this.right;
    }

    @Override
    public FloatCharMutablePair right(char r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof FloatCharPair) {
            return this.left == ((FloatCharPair)other).leftFloat() && this.right == ((FloatCharPair)other).rightChar();
        }
        if (other instanceof Pair) {
            return Objects.equals(Float.valueOf(this.left), ((Pair)other).left()) && Objects.equals(Character.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.float2int(this.left) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftFloat() + "," + this.rightChar() + ">";
    }
}

