/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongReferencePair;
import java.io.Serializable;
import java.util.Objects;

public class LongReferenceMutablePair<V>
implements LongReferencePair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected long left;
    protected V right;

    public LongReferenceMutablePair(long left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> LongReferenceMutablePair<V> of(long left, V right) {
        return new LongReferenceMutablePair<V>(left, right);
    }

    @Override
    public long leftLong() {
        return this.left;
    }

    @Override
    public LongReferenceMutablePair<V> left(long l) {
        this.left = l;
        return this;
    }

    @Override
    public V right() {
        return this.right;
    }

    public LongReferenceMutablePair<V> right(V r) {
        this.right = r;
        return this;
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

