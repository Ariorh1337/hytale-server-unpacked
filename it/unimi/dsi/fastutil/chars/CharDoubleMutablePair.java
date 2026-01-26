/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharDoublePair;
import java.io.Serializable;
import java.util.Objects;

public class CharDoubleMutablePair
implements CharDoublePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected char left;
    protected double right;

    public CharDoubleMutablePair(char left, double right) {
        this.left = left;
        this.right = right;
    }

    public static CharDoubleMutablePair of(char left, double right) {
        return new CharDoubleMutablePair(left, right);
    }

    @Override
    public char leftChar() {
        return this.left;
    }

    @Override
    public CharDoubleMutablePair left(char l) {
        this.left = l;
        return this;
    }

    @Override
    public double rightDouble() {
        return this.right;
    }

    @Override
    public CharDoubleMutablePair right(double r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharDoublePair) {
            return this.left == ((CharDoublePair)other).leftChar() && this.right == ((CharDoublePair)other).rightDouble();
        }
        if (other instanceof Pair) {
            return Objects.equals(Character.valueOf(this.left), ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + HashCommon.double2int(this.right);
    }

    public String toString() {
        return "<" + this.leftChar() + "," + this.rightDouble() + ">";
    }
}

