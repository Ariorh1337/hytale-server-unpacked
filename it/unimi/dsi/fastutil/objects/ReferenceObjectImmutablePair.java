/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceObjectPair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceObjectImmutablePair<K, V>
implements ReferenceObjectPair<K, V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final V right;

    public ReferenceObjectImmutablePair(K left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <K, V> ReferenceObjectImmutablePair<K, V> of(K left, V right) {
        return new ReferenceObjectImmutablePair<K, V>(left, right);
    }

    @Override
    public K left() {
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
        if (other instanceof Pair) {
            return this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return System.identityHashCode(this.left) * 19 + (this.right == null ? 0 : this.right.hashCode());
    }

    public String toString() {
        return "<" + this.left() + "," + this.right() + ">";
    }
}

