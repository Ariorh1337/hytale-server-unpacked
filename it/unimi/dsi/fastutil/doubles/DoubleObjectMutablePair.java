/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleObjectPair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleObjectMutablePair<V>
implements DoubleObjectPair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected double left;
    protected V right;

    public DoubleObjectMutablePair(double left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> DoubleObjectMutablePair<V> of(double left, V right) {
        return new DoubleObjectMutablePair<V>(left, right);
    }

    @Override
    public double leftDouble() {
        return this.left;
    }

    @Override
    public DoubleObjectMutablePair<V> left(double l) {
        this.left = l;
        return this;
    }

    @Override
    public V right() {
        return this.right;
    }

    public DoubleObjectMutablePair<V> right(V r) {
        this.right = r;
        return this;
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

