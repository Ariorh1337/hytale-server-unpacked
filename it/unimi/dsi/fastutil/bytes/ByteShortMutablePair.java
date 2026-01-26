/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteShortPair;
import java.io.Serializable;
import java.util.Objects;

public class ByteShortMutablePair
implements ByteShortPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected byte left;
    protected short right;

    public ByteShortMutablePair(byte left, short right) {
        this.left = left;
        this.right = right;
    }

    public static ByteShortMutablePair of(byte left, short right) {
        return new ByteShortMutablePair(left, right);
    }

    @Override
    public byte leftByte() {
        return this.left;
    }

    @Override
    public ByteShortMutablePair left(byte l) {
        this.left = l;
        return this;
    }

    @Override
    public short rightShort() {
        return this.right;
    }

    @Override
    public ByteShortMutablePair right(short r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ByteShortPair) {
            return this.left == ((ByteShortPair)other).leftByte() && this.right == ((ByteShortPair)other).rightShort();
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
        return "<" + this.leftByte() + "," + this.rightShort() + ">";
    }
}

