/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceShortPair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceShortMutablePair<K>
implements ReferenceShortPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected K left;
    protected short right;

    public ReferenceShortMutablePair(K left, short right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ReferenceShortMutablePair<K> of(K left, short right) {
        return new ReferenceShortMutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    public ReferenceShortMutablePair<K> left(K l) {
        this.left = l;
        return this;
    }

    @Override
    public short rightShort() {
        return this.right;
    }

    @Override
    public ReferenceShortMutablePair<K> right(short r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ReferenceShortPair) {
            return this.left == ((ReferenceShortPair)other).left() && this.right == ((ReferenceShortPair)other).rightShort();
        }
        if (other instanceof Pair) {
            return this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return System.identityHashCode(this.left) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightShort() + ">";
    }
}

