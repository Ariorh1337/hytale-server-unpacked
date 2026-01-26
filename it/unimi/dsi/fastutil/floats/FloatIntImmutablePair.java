/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatIntPair;
import java.io.Serializable;
import java.util.Objects;

public class FloatIntImmutablePair
implements FloatIntPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final float left;
    protected final int right;

    public FloatIntImmutablePair(float left, int right) {
        this.left = left;
        this.right = right;
    }

    public static FloatIntImmutablePair of(float left, int right) {
        return new FloatIntImmutablePair(left, right);
    }

    @Override
    public float leftFloat() {
        return this.left;
    }

    @Override
    public int rightInt() {
        return this.right;
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

