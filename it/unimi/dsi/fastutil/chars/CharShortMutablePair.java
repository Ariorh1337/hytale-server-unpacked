/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharShortPair;
import java.io.Serializable;
import java.util.Objects;

public class CharShortMutablePair
implements CharShortPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected char left;
    protected short right;

    public CharShortMutablePair(char left, short right) {
        this.left = left;
        this.right = right;
    }

    public static CharShortMutablePair of(char left, short right) {
        return new CharShortMutablePair(left, right);
    }

    @Override
    public char leftChar() {
        return this.left;
    }

    @Override
    public CharShortMutablePair left(char l) {
        this.left = l;
        return this;
    }

    @Override
    public short rightShort() {
        return this.right;
    }

    @Override
    public CharShortMutablePair right(short r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharShortPair) {
            return this.left == ((CharShortPair)other).leftChar() && this.right == ((CharShortPair)other).rightShort();
        }
        if (other instanceof Pair) {
            return Objects.equals(Character.valueOf(this.left), ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftChar() + "," + this.rightShort() + ">";
    }
}

