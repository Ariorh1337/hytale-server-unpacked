/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleDoublePair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleDoubleImmutablePair
implements DoubleDoublePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final double left;
    protected final double right;

    public DoubleDoubleImmutablePair(double left, double right) {
        this.left = left;
        this.right = right;
    }

    public static DoubleDoubleImmutablePair of(double left, double right) {
        return new DoubleDoubleImmutablePair(left, right);
    }

    @Override
    public double leftDouble() {
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
        if (other instanceof DoubleDoublePair) {
            return this.left == ((DoubleDoublePair)other).leftDouble() && this.right == ((DoubleDoublePair)other).rightDouble();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.double2int(this.left) * 19 + HashCommon.double2int(this.right);
    }

    public String toString() {
        return "<" + this.leftDouble() + "," + this.rightDouble() + ">";
    }
}

