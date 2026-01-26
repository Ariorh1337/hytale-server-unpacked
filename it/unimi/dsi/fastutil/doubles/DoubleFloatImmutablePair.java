/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleFloatPair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleFloatImmutablePair
implements DoubleFloatPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final double left;
    protected final float right;

    public DoubleFloatImmutablePair(double left, float right) {
        this.left = left;
        this.right = right;
    }

    public static DoubleFloatImmutablePair of(double left, float right) {
        return new DoubleFloatImmutablePair(left, right);
    }

    @Override
    public double leftDouble() {
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
        if (other instanceof DoubleFloatPair) {
            return this.left == ((DoubleFloatPair)other).leftDouble() && this.right == ((DoubleFloatPair)other).rightFloat();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(Float.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.double2int(this.left) * 19 + HashCommon.float2int(this.right);
    }

    public String toString() {
        return "<" + this.leftDouble() + "," + this.rightFloat() + ">";
    }
}

