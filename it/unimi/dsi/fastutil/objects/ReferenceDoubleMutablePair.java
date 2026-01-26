/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceDoublePair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceDoubleMutablePair<K>
implements ReferenceDoublePair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected K left;
    protected double right;

    public ReferenceDoubleMutablePair(K left, double right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ReferenceDoubleMutablePair<K> of(K left, double right) {
        return new ReferenceDoubleMutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    public ReferenceDoubleMutablePair<K> left(K l) {
        this.left = l;
        return this;
    }

    @Override
    public double rightDouble() {
        return this.right;
    }

    @Override
    public ReferenceDoubleMutablePair<K> right(double r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ReferenceDoublePair) {
            return this.left == ((ReferenceDoublePair)other).left() && this.right == ((ReferenceDoublePair)other).rightDouble();
        }
        if (other instanceof Pair) {
            return this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return System.identityHashCode(this.left) * 19 + HashCommon.double2int(this.right);
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightDouble() + ">";
    }
}

