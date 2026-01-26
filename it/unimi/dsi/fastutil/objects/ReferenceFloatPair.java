/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceFloatImmutablePair;

public interface ReferenceFloatPair<K>
extends Pair<K, Float> {
    public float rightFloat();

    @Override
    @Deprecated
    default public Float right() {
        return Float.valueOf(this.rightFloat());
    }

    default public ReferenceFloatPair<K> right(float r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ReferenceFloatPair<K> right(Float l) {
        return this.right(l.floatValue());
    }

    default public float secondFloat() {
        return this.rightFloat();
    }

    @Override
    @Deprecated
    default public Float second() {
        return Float.valueOf(this.secondFloat());
    }

    default public ReferenceFloatPair<K> second(float r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceFloatPair<K> second(Float l) {
        return this.second(l.floatValue());
    }

    default public float valueFloat() {
        return this.rightFloat();
    }

    @Override
    @Deprecated
    default public Float value() {
        return Float.valueOf(this.valueFloat());
    }

    default public ReferenceFloatPair<K> value(float r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceFloatPair<K> value(Float l) {
        return this.value(l.floatValue());
    }

    public static <K> ReferenceFloatPair<K> of(K left, float right) {
        return new ReferenceFloatImmutablePair<K>(left, right);
    }
}

