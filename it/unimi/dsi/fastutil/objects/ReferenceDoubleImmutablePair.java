/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceDoublePair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceDoubleImmutablePair<K>
implements ReferenceDoublePair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final double right;

    public ReferenceDoubleImmutablePair(K left, double right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ReferenceDoubleImmutablePair<K> of(K left, double right) {
        return new ReferenceDoubleImmutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    @Override
    public double rightDouble() {
        return this.right;
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

