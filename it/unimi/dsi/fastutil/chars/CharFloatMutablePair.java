/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharFloatPair;
import java.io.Serializable;
import java.util.Objects;

public class CharFloatMutablePair
implements CharFloatPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected char left;
    protected float right;

    public CharFloatMutablePair(char left, float right) {
        this.left = left;
        this.right = right;
    }

    public static CharFloatMutablePair of(char left, float right) {
        return new CharFloatMutablePair(left, right);
    }

    @Override
    public char leftChar() {
        return this.left;
    }

    @Override
    public CharFloatMutablePair left(char l) {
        this.left = l;
        return this;
    }

    @Override
    public float rightFloat() {
        return this.right;
    }

    @Override
    public CharFloatMutablePair right(float r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharFloatPair) {
            return this.left == ((CharFloatPair)other).leftChar() && this.right == ((CharFloatPair)other).rightFloat();
        }
        if (other instanceof Pair) {
            return Objects.equals(Character.valueOf(this.left), ((Pair)other).left()) && Objects.equals(Float.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + HashCommon.float2int(this.right);
    }

    public String toString() {
        return "<" + this.leftChar() + "," + this.rightFloat() + ">";
    }
}

