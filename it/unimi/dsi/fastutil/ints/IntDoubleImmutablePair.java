/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntDoublePair;
import java.io.Serializable;
import java.util.Objects;

public class IntDoubleImmutablePair
implements IntDoublePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final int left;
    protected final double right;

    public IntDoubleImmutablePair(int left, double right) {
        this.left = left;
        this.right = right;
    }

    public static IntDoubleImmutablePair of(int left, double right) {
        return new IntDoubleImmutablePair(left, right);
    }

    @Override
    public int leftInt() {
        return this.left;
    }

    @Override
    public double rightDouble() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntDoublePair) {
            return this.left == ((IntDoublePair)other).leftInt() && this.right == ((IntDoublePair)other).rightDouble();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + HashCommon.double2int(this.right);
    }

    public String toString() {
        return "<" + this.leftInt() + "," + this.rightDouble() + ">";
    }
}

