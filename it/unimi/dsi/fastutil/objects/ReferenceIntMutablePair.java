/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceIntPair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceIntMutablePair<K>
implements ReferenceIntPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected K left;
    protected int right;

    public ReferenceIntMutablePair(K left, int right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ReferenceIntMutablePair<K> of(K left, int right) {
        return new ReferenceIntMutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    public ReferenceIntMutablePair<K> left(K l) {
        this.left = l;
        return this;
    }

    @Override
    public int rightInt() {
        return this.right;
    }

    @Override
    public ReferenceIntMutablePair<K> right(int r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ReferenceIntPair) {
            return this.left == ((ReferenceIntPair)other).left() && this.right == ((ReferenceIntPair)other).rightInt();
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
        return "<" + this.left() + "," + this.rightInt() + ">";
    }
}

