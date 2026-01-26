/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatIntPair;
import java.io.Serializable;
import java.util.Objects;

public class FloatIntMutablePair
implements FloatIntPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected float left;
    protected int right;

    public FloatIntMutablePair(float left, int right) {
        this.left = left;
        this.right = right;
    }

    public static FloatIntMutablePair of(float left, int right) {
        return new FloatIntMutablePair(left, right);
    }

    @Override
    public float leftFloat() {
        return this.left;
    }

    @Override
    public FloatIntMutablePair left(float l) {
        this.left = l;
        return this;
    }

    @Override
    public int rightInt() {
        return this.right;
    }

    @Override
    public FloatIntMutablePair right(int r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof FloatIntPair) {
            return this.left == ((FloatIntPair)other).leftFloat() && this.right == ((FloatIntPair)other).rightInt();
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
        return "<" + this.leftFloat() + "," + this.rightInt() + ">";
    }
}

