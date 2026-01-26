/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanReferenceImmutablePair;

public interface BooleanReferencePair<V>
extends Pair<Boolean, V> {
    public boolean leftBoolean();

    @Override
    @Deprecated
    default public Boolean left() {
        return this.leftBoolean();
    }

    default public BooleanReferencePair<V> left(boolean l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanReferencePair<V> left(Boolean l) {
        return this.left((boolean)l);
    }

    default public boolean firstBoolean() {
        return this.leftBoolean();
    }

    @Override
    @Deprecated
    default public Boolean first() {
        return this.firstBoolean();
    }

    default public BooleanReferencePair<V> first(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanReferencePair<V> first(Boolean l) {
        return this.first((boolean)l);
    }

    default public boolean keyBoolean() {
        return this.firstBoolean();
    }

    @Override
    @Deprecated
    default public Boolean key() {
        return this.keyBoolean();
    }

    default public BooleanReferencePair<V> key(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanReferencePair<V> key(Boolean l) {
        return this.key((boolean)l);
    }

    public static <V> BooleanReferencePair<V> of(boolean left, V right) {
        return new BooleanReferenceImmutablePair<V>(left, right);
    }
}

