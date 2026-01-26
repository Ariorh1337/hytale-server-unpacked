/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatReferencePair;
import java.io.Serializable;
import java.util.Objects;

public class FloatReferenceImmutablePair<V>
implements FloatReferencePair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final float left;
    protected final V right;

    public FloatReferenceImmutablePair(float left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> FloatReferenceImmutablePair<V> of(float left, V right) {
        return new FloatReferenceImmutablePair<V>(left, right);
    }

    @Override
    public float leftFloat() {
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
        if (other instanceof FloatReferencePair) {
            return this.left == ((FloatReferencePair)other).leftFloat() && this.right == ((FloatReferencePair)other).right();
        }
        if (other instanceof Pair) {
            return Objects.equals(Float.valueOf(this.left), ((Pair)other).left()) && this.right == ((Pair)other).right();
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.float2int(this.left) * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
    }

    public String toString() {
        return "<" + this.leftFloat() + "," + this.right() + ">";
    }
}

