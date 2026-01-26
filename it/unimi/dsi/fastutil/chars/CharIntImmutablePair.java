/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharIntPair;
import java.io.Serializable;
import java.util.Objects;

public class CharIntImmutablePair
implements CharIntPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final char left;
    protected final int right;

    public CharIntImmutablePair(char left, int right) {
        this.left = left;
        this.right = right;
    }

    public static CharIntImmutablePair of(char left, int right) {
        return new CharIntImmutablePair(left, right);
    }

    @Override
    public char leftChar() {
        return this.left;
    }

    @Override
    public int rightInt() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharIntPair) {
            return this.left == ((CharIntPair)other).leftChar() && this.right == ((CharIntPair)other).rightInt();
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
        return "<" + this.leftChar() + "," + this.rightInt() + ">";
    }
}

