/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectReferencePair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectReferenceMutablePair<K, V>
implements ObjectReferencePair<K, V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected K left;
    protected V right;

    public ObjectReferenceMutablePair(K left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <K, V> ObjectReferenceMutablePair<K, V> of(K left, V right) {
        return new ObjectReferenceMutablePair<K, V>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    @Override
    public ObjectReferenceMutablePair<K, V> left(K l) {
        this.left = l;
        return this;
    }

    @Override
    public V right() {
        return this.right;
    }

    @Override
    public ObjectReferenceMutablePair<K, V> right(V r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && this.right == ((Pair)other).right();
        }
        return false;
    }

    public int hashCode() {
        return (this.left == null ? 0 : this.left.hashCode()) * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
    }

    public String toString() {
        return "<" + this.left() + "," + this.right() + ">";
    }
}

