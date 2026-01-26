/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceFloatPair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceFloatImmutablePair<K>
implements ReferenceFloatPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final float right;

    public ReferenceFloatImmutablePair(K left, float right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ReferenceFloatImmutablePair<K> of(K left, float right) {
        return new ReferenceFloatImmutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    @Override
    public float rightFloat() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ReferenceFloatPair) {
            return this.left == ((ReferenceFloatPair)other).left() && this.right == ((ReferenceFloatPair)other).rightFloat();
        }
        if (other instanceof Pair) {
            return this.left == ((Pair)other).left() && Objects.equals(Float.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return System.identityHashCode(this.left) * 19 + HashCommon.float2int(this.right);
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightFloat() + ">";
    }
}

