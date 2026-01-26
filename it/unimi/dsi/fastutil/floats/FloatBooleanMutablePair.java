/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatBooleanPair;
import java.io.Serializable;
import java.util.Objects;

public class FloatBooleanMutablePair
implements FloatBooleanPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected float left;
    protected boolean right;

    public FloatBooleanMutablePair(float left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public static FloatBooleanMutablePair of(float left, boolean right) {
        return new FloatBooleanMutablePair(left, right);
    }

    @Override
    public float leftFloat() {
        return this.left;
    }

    @Override
    public FloatBooleanMutablePair left(float l) {
        this.left = l;
        return this;
    }

    @Override
    public boolean rightBoolean() {
        return this.right;
    }

    @Override
    public FloatBooleanMutablePair right(boolean r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof FloatBooleanPair) {
            return this.left == ((FloatBooleanPair)other).leftFloat() && this.right == ((FloatBooleanPair)other).rightBoolean();
        }
        if (other instanceof Pair) {
            return Objects.equals(Float.valueOf(this.left), ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.float2int(this.left) * 19 + (this.right ? 1231 : 1237);
    }

    public String toString() {
        return "<" + this.leftFloat() + "," + this.rightBoolean() + ">";
    }
}

