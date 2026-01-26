/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteBytePair;
import java.io.Serializable;
import java.util.Objects;

public class ByteByteMutablePair
implements ByteBytePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected byte left;
    protected byte right;

    public ByteByteMutablePair(byte left, byte right) {
        this.left = left;
        this.right = right;
    }

    public static ByteByteMutablePair of(byte left, byte right) {
        return new ByteByteMutablePair(left, right);
    }

    @Override
    public byte leftByte() {
        return this.left;
    }

    @Override
    public ByteByteMutablePair left(byte l) {
        this.left = l;
        return this;
    }

    @Override
    public byte rightByte() {
        return this.right;
    }

    @Override
    public ByteByteMutablePair right(byte r) {
        this.right = r;
        return this;
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

