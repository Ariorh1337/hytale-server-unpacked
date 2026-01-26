/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortObjectPair;
import java.io.Serializable;
import java.util.Objects;

public class ShortObjectImmutablePair<V>
implements ShortObjectPair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final short left;
    protected final V right;

    public ShortObjectImmutablePair(short left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> ShortObjectImmutablePair<V> of(short left, V right) {
        return new ShortObjectImmutablePair<V>(left, right);
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
        if (other instanceof ShortObjectPair) {
            return this.left == ((ShortObjectPair)other).leftShort() && Objects.equals(this.right, ((ShortObjectPair)other).right());
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + (this.right == null ? 0 : this.right.hashCode());
    }

    public String toString() {
        return "<" + this.leftShort() + "," + this.right() + ">";
    }
}

