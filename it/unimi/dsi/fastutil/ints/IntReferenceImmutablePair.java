/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntReferencePair;
import java.io.Serializable;
import java.util.Objects;

public class IntReferenceImmutablePair<V>
implements IntReferencePair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final int left;
    protected final V right;

    public IntReferenceImmutablePair(int left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> IntReferenceImmutablePair<V> of(int left, V right) {
        return new IntReferenceImmutablePair<V>(left, right);
    }

    @Override
    public int leftInt() {
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
        if (other instanceof IntReferencePair) {
            return this.left == ((IntReferencePair)other).leftInt() && this.right == ((IntReferencePair)other).right();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && this.right == ((Pair)other).right();
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
    }

    public String toString() {
        return "<" + this.leftInt() + "," + this.right() + ">";
    }
}

