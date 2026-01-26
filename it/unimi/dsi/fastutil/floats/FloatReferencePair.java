/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatReferenceImmutablePair;

public interface FloatReferencePair<V>
extends Pair<Float, V> {
    public float leftFloat();

    @Override
    @Deprecated
    default public Float left() {
        return Float.valueOf(this.leftFloat());
    }

    default public FloatReferencePair<V> left(float l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatReferencePair<V> left(Float l) {
        return this.left(l.floatValue());
    }

    default public float firstFloat() {
        return this.leftFloat();
    }

    @Override
    @Deprecated
    default public Float first() {
        return Float.valueOf(this.firstFloat());
    }

    default public FloatReferencePair<V> first(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatReferencePair<V> first(Float l) {
        return this.first(l.floatValue());
    }

    default public float keyFloat() {
        return this.firstFloat();
    }

    @Override
    @Deprecated
    default public Float key() {
        return Float.valueOf(this.keyFloat());
    }

    default public FloatReferencePair<V> key(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatReferencePair<V> key(Float l) {
        return this.key(l.floatValue());
    }

    public static <V> FloatReferencePair<V> of(float left, V right) {
        return new FloatReferenceImmutablePair<V>(left, right);
    }
}

