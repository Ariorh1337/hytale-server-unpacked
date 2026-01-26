/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleIntPair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleIntImmutablePair
implements DoubleIntPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final double left;
    protected final int right;

    public DoubleIntImmutablePair(double left, int right) {
        this.left = left;
        this.right = right;
    }

    public static DoubleIntImmutablePair of(double left, int right) {
        return new DoubleIntImmutablePair(left, right);
    }

    @Override
    public double leftDouble() {
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
        if (other instanceof DoubleIntPair) {
            return this.left == ((DoubleIntPair)other).leftDouble() && this.right == ((DoubleIntPair)other).rightInt();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.double2int(this.left) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftDouble() + "," + this.rightInt() + ">";
    }
}

