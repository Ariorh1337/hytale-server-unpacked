/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntReferenceImmutablePair;

public interface IntReferencePair<V>
extends Pair<Integer, V> {
    public int leftInt();

    @Override
    @Deprecated
    default public Integer left() {
        return this.leftInt();
    }

    default public IntReferencePair<V> left(int l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntReferencePair<V> left(Integer l) {
        return this.left((int)l);
    }

    default public int firstInt() {
        return this.leftInt();
    }

    @Override
    @Deprecated
    default public Integer first() {
        return this.firstInt();
    }

    default public IntReferencePair<V> first(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntReferencePair<V> first(Integer l) {
        return this.first((int)l);
    }

    default public int keyInt() {
        return this.firstInt();
    }

    @Override
    @Deprecated
    default public Integer key() {
        return this.keyInt();
    }

    default public IntReferencePair<V> key(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntReferencePair<V> key(Integer l) {
        return this.key((int)l);
    }

    public static <V> IntReferencePair<V> of(int left, V right) {
        return new IntReferenceImmutablePair<V>(left, right);
    }
}

