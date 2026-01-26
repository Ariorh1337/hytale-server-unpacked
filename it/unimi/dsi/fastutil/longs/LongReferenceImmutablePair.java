/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongReferencePair;
import java.io.Serializable;
import java.util.Objects;

public class LongReferenceImmutablePair<V>
implements LongReferencePair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final long left;
    protected final V right;

    public LongReferenceImmutablePair(long left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> LongReferenceImmutablePair<V> of(long left, V right) {
        return new LongReferenceImmutablePair<V>(left, right);
    }

    @Override
    public long leftLong() {
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
        if (other instanceof LongReferencePair) {
            return this.left == ((LongReferencePair)other).leftLong() && this.right == ((LongReferencePair)other).right();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && this.right == ((Pair)other).right();
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.long2int(this.left) * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
    }

    public String toString() {
        return "<" + this.leftLong() + "," + this.right() + ">";
    }
}

