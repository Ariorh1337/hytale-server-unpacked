/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteObjectImmutablePair;
import java.util.Comparator;

public interface ByteObjectPair<V>
extends Pair<Byte, V> {
    public byte leftByte();

    @Override
    @Deprecated
    default public Byte left() {
        return this.leftByte();
    }

    default public ByteObjectPair<V> left(byte l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteObjectPair<V> left(Byte l) {
        return this.left((byte)l);
    }

    default public byte firstByte() {
        return this.leftByte();
    }

    @Override
    @Deprecated
    default public Byte first() {
        return this.firstByte();
    }

    default public ByteObjectPair<V> first(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteObjectPair<V> first(Byte l) {
        return this.first((byte)l);
    }

    default public byte keyByte() {
        return this.firstByte();
    }

    @Override
    @Deprecated
    default public Byte key() {
        return this.keyByte();
    }

    default public ByteObjectPair<V> key(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteObjectPair<V> key(Byte l) {
        return this.key((byte)l);
    }

    public static <V> ByteObjectPair<V> of(byte left, V right) {
        return new ByteObjectImmutablePair<V>(left, right);
    }

    public static <V> Comparator<ByteObjectPair<V>> lexComparator() {
        return (x, y) -> {
            int t = Byte.compare(x.leftByte(), y.leftByte());
            if (t != 0) {
                return t;
            }
            return ((Comparable)x.right()).compareTo(y.right());
        };
    }
}

