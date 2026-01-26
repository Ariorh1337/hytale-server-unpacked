/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortFloatPair;
import java.io.Serializable;
import java.util.Objects;

public class ShortFloatImmutablePair
implements ShortFloatPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final short left;
    protected final float right;

    public ShortFloatImmutablePair(short left, float right) {
        this.left = left;
        this.right = right;
    }

    public static ShortFloatImmutablePair of(short left, float right) {
        return new ShortFloatImmutablePair(left, right);
    }

    @Override
    public short leftShort() {
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
        if (other instanceof ShortFloatPair) {
            return this.left == ((ShortFloatPair)other).leftShort() && this.right == ((ShortFloatPair)other).rightFloat();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(Float.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + HashCommon.float2int(this.right);
    }

    public String toString() {
        return "<" + this.leftShort() + "," + this.rightFloat() + ">";
    }
}

