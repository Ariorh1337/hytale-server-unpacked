/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongObjectPair;
import java.io.Serializable;
import java.util.Objects;

public class LongObjectImmutablePair<V>
implements LongObjectPair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final long left;
    protected final V right;

    public LongObjectImmutablePair(long left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> LongObjectImmutablePair<V> of(long left, V right) {
        return new LongObjectImmutablePair<V>(left, right);
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
        if (other instanceof LongObjectPair) {
            return this.left == ((LongObjectPair)other).leftLong() && Objects.equals(this.right, ((LongObjectPair)other).right());
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.long2int(this.left) * 19 + (this.right == null ? 0 : this.right.hashCode());
    }

    public String toString() {
        return "<" + this.leftLong() + "," + this.right() + ">";
    }
}

