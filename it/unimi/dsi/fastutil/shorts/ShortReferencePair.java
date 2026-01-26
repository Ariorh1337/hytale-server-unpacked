/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortReferenceImmutablePair;

public interface ShortReferencePair<V>
extends Pair<Short, V> {
    public short leftShort();

    @Override
    @Deprecated
    default public Short left() {
        return this.leftShort();
    }

    default public ShortReferencePair<V> left(short l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortReferencePair<V> left(Short l) {
        return this.left((short)l);
    }

    default public short firstShort() {
        return this.leftShort();
    }

    @Override
    @Deprecated
    default public Short first() {
        return this.firstShort();
    }

    default public ShortReferencePair<V> first(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortReferencePair<V> first(Short l) {
        return this.first((short)l);
    }

    default public short keyShort() {
        return this.firstShort();
    }

    @Override
    @Deprecated
    default public Short key() {
        return this.keyShort();
    }

    default public ShortReferencePair<V> key(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortReferencePair<V> key(Short l) {
        return this.key((short)l);
    }

    public static <V> ShortReferencePair<V> of(short left, V right) {
        return new ShortReferenceImmutablePair<V>(left, right);
    }
}

