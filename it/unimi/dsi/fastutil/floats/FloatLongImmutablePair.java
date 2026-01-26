/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatLongPair;
import java.io.Serializable;
import java.util.Objects;

public class FloatLongImmutablePair
implements FloatLongPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final float left;
    protected final long right;

    public FloatLongImmutablePair(float left, long right) {
        this.left = left;
        this.right = right;
    }

    public static FloatLongImmutablePair of(float left, long right) {
        return new FloatLongImmutablePair(left, right);
    }

    @Override
    public float leftFloat() {
        return this.left;
    }

    @Override
    public long rightLong() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof FloatLongPair) {
            return this.left == ((FloatLongPair)other).leftFloat() && this.right == ((FloatLongPair)other).rightLong();
        }
        if (other instanceof Pair) {
            return Objects.equals(Float.valueOf(this.left), ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.float2int(this.left) * 19 + HashCommon.long2int(this.right);
    }

    public String toString() {
        return "<" + this.leftFloat() + "," + this.rightLong() + ">";
    }
}

