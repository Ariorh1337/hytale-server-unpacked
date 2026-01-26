/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.bytes.ByteByteImmutableSortedPair;
import it.unimi.dsi.fastutil.bytes.ByteBytePair;
import java.io.Serializable;

public interface ByteByteSortedPair
extends ByteBytePair,
SortedPair<Byte>,
Serializable {
    public static ByteByteSortedPair of(byte left, byte right) {
        return ByteByteImmutableSortedPair.of(left, right);
    }

    default public boolean contains(byte e) {
        return e == this.leftByte() || e == this.rightByte();
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.contains((Byte)o);
    }
}

