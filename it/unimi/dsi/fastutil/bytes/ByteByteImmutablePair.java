/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteBytePair;
import java.io.Serializable;
import java.util.Objects;

public class ByteByteImmutablePair
implements ByteBytePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final byte left;
    protected final byte right;

    public ByteByteImmutablePair(byte left, byte right) {
        this.left = left;
        this.right = right;
    }

    public static ByteByteImmutablePair of(byte left, byte right) {
        return new ByteByteImmutablePair(left, right);
    }

    @Override
    public byte leftByte() {
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
        if (other instanceof ByteBytePair) {
            return this.left == ((ByteBytePair)other).leftByte() && this.right == ((ByteBytePair)other).rightByte();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftByte() + "," + this.rightByte() + ">";
    }
}

