/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharBytePair;
import java.io.Serializable;
import java.util.Objects;

public class CharByteImmutablePair
implements CharBytePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final char left;
    protected final byte right;

    public CharByteImmutablePair(char left, byte right) {
        this.left = left;
        this.right = right;
    }

    public static CharByteImmutablePair of(char left, byte right) {
        return new CharByteImmutablePair(left, right);
    }

    @Override
    public char leftChar() {
        return this.left;
    }

    @Override
    public byte rightByte() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharBytePair) {
            return this.left == ((CharBytePair)other).leftChar() && this.right == ((CharBytePair)other).rightByte();
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
        return "<" + this.leftChar() + "," + this.rightByte() + ">";
    }
}

