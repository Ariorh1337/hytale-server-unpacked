/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteCharPair;
import java.io.Serializable;
import java.util.Objects;

public class ByteCharImmutablePair
implements ByteCharPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final byte left;
    protected final char right;

    public ByteCharImmutablePair(byte left, char right) {
        this.left = left;
        this.right = right;
    }

    public static ByteCharImmutablePair of(byte left, char right) {
        return new ByteCharImmutablePair(left, right);
    }

    @Override
    public byte leftByte() {
        return this.left;
    }

    @Override
    public char rightChar() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ByteCharPair) {
            return this.left == ((ByteCharPair)other).leftByte() && this.right == ((ByteCharPair)other).rightChar();
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
        return "<" + this.leftByte() + "," + this.rightChar() + ">";
    }
}

