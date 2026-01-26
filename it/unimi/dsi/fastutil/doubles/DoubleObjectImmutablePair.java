/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleObjectPair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleObjectImmutablePair<V>
implements DoubleObjectPair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final double left;
    protected final V right;

    public DoubleObjectImmutablePair(double left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> DoubleObjectImmutablePair<V> of(double left, V right) {
        return new DoubleObjectImmutablePair<V>(left, right);
    }

    @Override
    public double leftDouble() {
        return this.left;
    }

    @Override
    public V right() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof DoubleObjectPair) {
            return this.left == ((DoubleObjectPair)other).leftDouble() && Objects.equals(this.right, ((DoubleObjectPair)other).right());
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.double2int(this.left) * 19 + (this.right == null ? 0 : this.right.hashCode());
    }

    public String toString() {
        return "<" + this.leftDouble() + "," + this.right() + ">";
    }
}

