/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongDoublePair;
import java.io.Serializable;
import java.util.Objects;

public class LongDoubleMutablePair
implements LongDoublePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected long left;
    protected double right;

    public LongDoubleMutablePair(long left, double right) {
        this.left = left;
        this.right = right;
    }

    public static LongDoubleMutablePair of(long left, double right) {
        return new LongDoubleMutablePair(left, right);
    }

    @Override
    public long leftLong() {
        return this.left;
    }

    @Override
    public LongDoubleMutablePair left(long l) {
        this.left = l;
        return this;
    }

    @Override
    public double rightDouble() {
        return this.right;
    }

    @Override
    public LongDoubleMutablePair right(double r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof LongDoublePair) {
            return this.left == ((LongDoublePair)other).leftLong() && this.right == ((LongDoublePair)other).rightDouble();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.long2int(this.left) * 19 + HashCommon.double2int(this.right);
    }

    public String toString() {
        return "<" + this.leftLong() + "," + this.rightDouble() + ">";
    }
}

