/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatShortPair;
import java.io.Serializable;
import java.util.Objects;

public class FloatShortMutablePair
implements FloatShortPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected float left;
    protected short right;

    public FloatShortMutablePair(float left, short right) {
        this.left = left;
        this.right = right;
    }

    public static FloatShortMutablePair of(float left, short right) {
        return new FloatShortMutablePair(left, right);
    }

    @Override
    public float leftFloat() {
        return this.left;
    }

    @Override
    public FloatShortMutablePair left(float l) {
        this.left = l;
        return this;
    }

    @Override
    public short rightShort() {
        return this.right;
    }

    @Override
    public FloatShortMutablePair right(short r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof FloatShortPair) {
            return this.left == ((FloatShortPair)other).leftFloat() && this.right == ((FloatShortPair)other).rightShort();
        }
        if (other instanceof Pair) {
            return Objects.equals(Float.valueOf(this.left), ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.float2int(this.left) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftFloat() + "," + this.rightShort() + ">";
    }
}

