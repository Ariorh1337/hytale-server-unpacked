/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatBytePair;
import java.io.Serializable;
import java.util.Objects;

public class FloatByteImmutablePair
implements FloatBytePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final float left;
    protected final byte right;

    public FloatByteImmutablePair(float left, byte right) {
        this.left = left;
        this.right = right;
    }

    public static FloatByteImmutablePair of(float left, byte right) {
        return new FloatByteImmutablePair(left, right);
    }

    @Override
    public float leftFloat() {
        return this.left;
    }

    @Override
    public byte rightByte() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof FloatBytePair) {
            return this.left == ((FloatBytePair)other).leftFloat() && this.right == ((FloatBytePair)other).rightByte();
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
        return "<" + this.leftFloat() + "," + this.rightByte() + ">";
    }
}

