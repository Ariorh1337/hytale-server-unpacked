/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortReferencePair;
import java.io.Serializable;
import java.util.Objects;

public class ShortReferenceImmutablePair<V>
implements ShortReferencePair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final short left;
    protected final V right;

    public ShortReferenceImmutablePair(short left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> ShortReferenceImmutablePair<V> of(short left, V right) {
        return new ShortReferenceImmutablePair<V>(left, right);
    }

    @Override
    public short leftShort() {
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
        if (other instanceof ShortReferencePair) {
            return this.left == ((ShortReferencePair)other).leftShort() && this.right == ((ShortReferencePair)other).right();
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
        return "<" + this.leftShort() + "," + this.right() + ">";
    }
}

