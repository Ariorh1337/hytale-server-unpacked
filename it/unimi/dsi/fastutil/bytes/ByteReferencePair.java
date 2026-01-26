/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteReferenceImmutablePair;

public interface ByteReferencePair<V>
extends Pair<Byte, V> {
    public byte leftByte();

    @Override
    @Deprecated
    default public Byte left() {
        return this.leftByte();
    }

    default public ByteReferencePair<V> left(byte l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteReferencePair<V> left(Byte l) {
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

    default public ByteReferencePair<V> first(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteReferencePair<V> first(Byte l) {
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

    default public ByteReferencePair<V> key(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteReferencePair<V> key(Byte l) {
        return this.key((byte)l);
    }

    public static <V> ByteReferencePair<V> of(byte left, V right) {
        return new ByteReferenceImmutablePair<V>(left, right);
    }
}

