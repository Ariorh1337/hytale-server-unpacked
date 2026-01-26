/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteObjectPair;
import java.io.Serializable;
import java.util.Objects;

public class ByteObjectImmutablePair<V>
implements ByteObjectPair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final byte left;
    protected final V right;

    public ByteObjectImmutablePair(byte left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> ByteObjectImmutablePair<V> of(byte left, V right) {
        return new ByteObjectImmutablePair<V>(left, right);
    }

    @Override
    public byte leftByte() {
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
        if (other instanceof ByteObjectPair) {
            return this.left == ((ByteObjectPair)other).leftByte() && Objects.equals(this.right, ((ByteObjectPair)other).right());
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + (this.right == null ? 0 : this.right.hashCode());
    }

    public String toString() {
        return "<" + this.leftByte() + "," + this.right() + ">";
    }
}

