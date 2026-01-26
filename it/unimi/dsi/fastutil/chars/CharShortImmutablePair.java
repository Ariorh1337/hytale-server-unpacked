/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharShortPair;
import java.io.Serializable;
import java.util.Objects;

public class CharShortImmutablePair
implements CharShortPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final char left;
    protected final short right;

    public CharShortImmutablePair(char left, short right) {
        this.left = left;
        this.right = right;
    }

    public static CharShortImmutablePair of(char left, short right) {
        return new CharShortImmutablePair(left, right);
    }

    @Override
    public char leftChar() {
        return this.left;
    }

    @Override
    public short rightShort() {
        return this.right;
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

