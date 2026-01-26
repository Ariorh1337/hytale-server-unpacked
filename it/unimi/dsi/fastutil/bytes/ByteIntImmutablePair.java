/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteIntPair;
import java.io.Serializable;
import java.util.Objects;

public class ByteIntImmutablePair
implements ByteIntPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final byte left;
    protected final int right;

    public ByteIntImmutablePair(byte left, int right) {
        this.left = left;
        this.right = right;
    }

    public static ByteIntImmutablePair of(byte left, int right) {
        return new ByteIntImmutablePair(left, right);
    }

    @Override
    public byte leftByte() {
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
        if (other instanceof ByteIntPair) {
            return this.left == ((ByteIntPair)other).leftByte() && this.right == ((ByteIntPair)other).rightInt();
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
        return "<" + this.leftByte() + "," + this.rightInt() + ">";
    }
}

