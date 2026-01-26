/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongBytePair;
import java.io.Serializable;
import java.util.Objects;

public class LongByteMutablePair
implements LongBytePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected long left;
    protected byte right;

    public LongByteMutablePair(long left, byte right) {
        this.left = left;
        this.right = right;
    }

    public static LongByteMutablePair of(long left, byte right) {
        return new LongByteMutablePair(left, right);
    }

    @Override
    public long leftLong() {
        return this.left;
    }

    @Override
    public LongByteMutablePair left(long l) {
        this.left = l;
        return this;
    }

    @Override
    public byte rightByte() {
        return this.right;
    }

    @Override
    public LongByteMutablePair right(byte r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof LongBytePair) {
            return this.left == ((LongBytePair)other).leftLong() && this.right == ((LongBytePair)other).rightByte();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.long2int(this.left) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftLong() + "," + this.rightByte() + ">";
    }
}

