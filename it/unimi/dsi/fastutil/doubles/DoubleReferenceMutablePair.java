/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleReferencePair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleReferenceMutablePair<V>
implements DoubleReferencePair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected double left;
    protected V right;

    public DoubleReferenceMutablePair(double left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> DoubleReferenceMutablePair<V> of(double left, V right) {
        return new DoubleReferenceMutablePair<V>(left, right);
    }

    @Override
    public double leftDouble() {
        return this.left;
    }

    @Override
    public DoubleReferenceMutablePair<V> left(double l) {
        this.left = l;
        return this;
    }

    @Override
    public V right() {
        return this.right;
    }

    public DoubleReferenceMutablePair<V> right(V r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof DoubleReferencePair) {
            return this.left == ((DoubleReferencePair)other).leftDouble() && this.right == ((DoubleReferencePair)other).right();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && this.right == ((Pair)other).right();
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.double2int(this.left) * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
    }

    public String toString() {
        return "<" + this.leftDouble() + "," + this.right() + ">";
    }
}

