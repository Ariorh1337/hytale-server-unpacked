/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceByteImmutablePair;

public interface ReferenceBytePair<K>
extends Pair<K, Byte> {
    public byte rightByte();

    @Override
    @Deprecated
    default public Byte right() {
        return this.rightByte();
    }

    default public ReferenceBytePair<K> right(byte r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ReferenceBytePair<K> right(Byte l) {
        return this.right((byte)l);
    }

    default public byte secondByte() {
        return this.rightByte();
    }

    @Override
    @Deprecated
    default public Byte second() {
        return this.secondByte();
    }

    default public ReferenceBytePair<K> second(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceBytePair<K> second(Byte l) {
        return this.second((byte)l);
    }

    default public byte valueByte() {
        return this.rightByte();
    }

    @Override
    @Deprecated
    default public Byte value() {
        return this.valueByte();
    }

    default public ReferenceBytePair<K> value(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceBytePair<K> value(Byte l) {
        return this.value((byte)l);
    }

    public static <K> ReferenceBytePair<K> of(K left, byte right) {
        return new ReferenceByteImmutablePair<K>(left, right);
    }
}

