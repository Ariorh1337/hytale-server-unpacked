/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatDoublePair;
import java.io.Serializable;
import java.util.Objects;

public class FloatDoubleMutablePair
implements FloatDoublePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected float left;
    protected double right;

    public FloatDoubleMutablePair(float left, double right) {
        this.left = left;
        this.right = right;
    }

    public static FloatDoubleMutablePair of(float left, double right) {
        return new FloatDoubleMutablePair(left, right);
    }

    @Override
    public float leftFloat() {
        return this.left;
    }

    @Override
    public FloatDoubleMutablePair left(float l) {
        this.left = l;
        return this;
    }

    @Override
    public double rightDouble() {
        return this.right;
    }

    @Override
    public FloatDoubleMutablePair right(double r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof FloatDoublePair) {
            return this.left == ((FloatDoublePair)other).leftFloat() && this.right == ((FloatDoublePair)other).rightDouble();
        }
        if (other instanceof Pair) {
            return Objects.equals(Float.valueOf(this.left), ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.float2int(this.left) * 19 + HashCommon.double2int(this.right);
    }

    public String toString() {
        return "<" + this.leftFloat() + "," + this.rightDouble() + ">";
    }
}

