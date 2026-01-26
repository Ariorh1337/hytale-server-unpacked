/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatFloatPair;
import java.io.Serializable;
import java.util.Objects;

public class FloatFloatImmutablePair
implements FloatFloatPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final float left;
    protected final float right;

    public FloatFloatImmutablePair(float left, float right) {
        this.left = left;
        this.right = right;
    }

    public static FloatFloatImmutablePair of(float left, float right) {
        return new FloatFloatImmutablePair(left, right);
    }

    @Override
    public float leftFloat() {
        return this.left;
    }

    @Override
    public float rightFloat() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof FloatFloatPair) {
            return this.left == ((FloatFloatPair)other).leftFloat() && this.right == ((FloatFloatPair)other).rightFloat();
        }
        if (other instanceof Pair) {
            return Objects.equals(Float.valueOf(this.left), ((Pair)other).left()) && Objects.equals(Float.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.float2int(this.left) * 19 + HashCommon.float2int(this.right);
    }

    public String toString() {
        return "<" + this.leftFloat() + "," + this.rightFloat() + ">";
    }
}

