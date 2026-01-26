/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleReferencePair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleReferenceImmutablePair<V>
implements DoubleReferencePair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final double left;
    protected final V right;

    public DoubleReferenceImmutablePair(double left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> DoubleReferenceImmutablePair<V> of(double left, V right) {
        return new DoubleReferenceImmutablePair<V>(left, right);
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

