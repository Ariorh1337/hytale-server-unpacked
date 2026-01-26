/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortIntPair;
import java.io.Serializable;
import java.util.Objects;

public class ShortIntMutablePair
implements ShortIntPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected short left;
    protected int right;

    public ShortIntMutablePair(short left, int right) {
        this.left = left;
        this.right = right;
    }

    public static ShortIntMutablePair of(short left, int right) {
        return new ShortIntMutablePair(left, right);
    }

    @Override
    public short leftShort() {
        return this.left;
    }

    @Override
    public ShortIntMutablePair left(short l) {
        this.left = l;
        return this;
    }

    @Override
    public int rightInt() {
        return this.right;
    }

    @Override
    public ShortIntMutablePair right(int r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ShortIntPair) {
            return this.left == ((ShortIntPair)other).leftShort() && this.right == ((ShortIntPair)other).rightInt();
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
        return "<" + this.leftShort() + "," + this.rightInt() + ">";
    }
}

