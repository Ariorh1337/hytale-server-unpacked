/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteFloatPair;
import java.io.Serializable;
import java.util.Objects;

public class ByteFloatMutablePair
implements ByteFloatPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected byte left;
    protected float right;

    public ByteFloatMutablePair(byte left, float right) {
        this.left = left;
        this.right = right;
    }

    public static ByteFloatMutablePair of(byte left, float right) {
        return new ByteFloatMutablePair(left, right);
    }

    @Override
    public byte leftByte() {
        return this.left;
    }

    @Override
    public ByteFloatMutablePair left(byte l) {
        this.left = l;
        return this;
    }

    @Override
    public float rightFloat() {
        return this.right;
    }

    @Override
    public ByteFloatMutablePair right(float r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ByteFloatPair) {
            return this.left == ((ByteFloatPair)other).leftByte() && this.right == ((ByteFloatPair)other).rightFloat();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(Float.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + HashCommon.float2int(this.right);
    }

    public String toString() {
        return "<" + this.leftByte() + "," + this.rightFloat() + ">";
    }
}

