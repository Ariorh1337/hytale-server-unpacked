/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteDoublePair;
import java.io.Serializable;
import java.util.Objects;

public class ByteDoubleMutablePair
implements ByteDoublePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected byte left;
    protected double right;

    public ByteDoubleMutablePair(byte left, double right) {
        this.left = left;
        this.right = right;
    }

    public static ByteDoubleMutablePair of(byte left, double right) {
        return new ByteDoubleMutablePair(left, right);
    }

    @Override
    public byte leftByte() {
        return this.left;
    }

    @Override
    public ByteDoubleMutablePair left(byte l) {
        this.left = l;
        return this;
    }

    @Override
    public double rightDouble() {
        return this.right;
    }

    @Override
    public ByteDoubleMutablePair right(double r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ByteDoublePair) {
            return this.left == ((ByteDoublePair)other).leftByte() && this.right == ((ByteDoublePair)other).rightDouble();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + HashCommon.double2int(this.right);
    }

    public String toString() {
        return "<" + this.leftByte() + "," + this.rightDouble() + ">";
    }
}

