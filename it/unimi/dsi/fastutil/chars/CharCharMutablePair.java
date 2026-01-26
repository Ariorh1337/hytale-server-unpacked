/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharCharPair;
import java.io.Serializable;
import java.util.Objects;

public class CharCharMutablePair
implements CharCharPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected char left;
    protected char right;

    public CharCharMutablePair(char left, char right) {
        this.left = left;
        this.right = right;
    }

    public static CharCharMutablePair of(char left, char right) {
        return new CharCharMutablePair(left, right);
    }

    @Override
    public char leftChar() {
        return this.left;
    }

    @Override
    public CharCharMutablePair left(char l) {
        this.left = l;
        return this;
    }

    @Override
    public char rightChar() {
        return this.right;
    }

    @Override
    public CharCharMutablePair right(char r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharCharPair) {
            return this.left == ((CharCharPair)other).leftChar() && this.right == ((CharCharPair)other).rightChar();
        }
        if (other instanceof Pair) {
            return Objects.equals(Character.valueOf(this.left), ((Pair)other).left()) && Objects.equals(Character.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftChar() + "," + this.rightChar() + ">";
    }
}

