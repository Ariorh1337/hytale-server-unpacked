/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.bytes.ByteByteImmutablePair;
import it.unimi.dsi.fastutil.bytes.ByteByteSortedPair;
import java.io.Serializable;
import java.util.Objects;

public class ByteByteImmutableSortedPair
extends ByteByteImmutablePair
implements ByteByteSortedPair,
Serializable {
    private static final long serialVersionUID = 0L;

    private ByteByteImmutableSortedPair(byte left, byte right) {
        super(left, right);
    }

    public static ByteByteImmutableSortedPair of(byte left, byte right) {
        if (left <= right) {
            return new ByteByteImmutableSortedPair(left, right);
        }
        return new ByteByteImmutableSortedPair(right, left);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ByteByteSortedPair) {
            return this.left == ((ByteByteSortedPair)other).leftByte() && this.right == ((ByteByteSortedPair)other).rightByte();
        }
        if (other instanceof SortedPair) {
            return Objects.equals(this.left, ((SortedPair)other).left()) && Objects.equals(this.right, ((SortedPair)other).right());
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + this.leftByte() + "," + this.rightByte() + "}";
    }
}

