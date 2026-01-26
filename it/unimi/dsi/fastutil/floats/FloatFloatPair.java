/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatFloatImmutablePair;
import java.util.Comparator;

public interface FloatFloatPair
extends Pair<Float, Float> {
    public float leftFloat();

    @Override
    @Deprecated
    default public Float left() {
        return Float.valueOf(this.leftFloat());
    }

    default public FloatFloatPair left(float l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatFloatPair left(Float l) {
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

    default public FloatFloatPair first(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatFloatPair first(Float l) {
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

    default public FloatFloatPair key(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatFloatPair key(Float l) {
        return this.key(l.floatValue());
    }

    public float rightFloat();

    @Override
    @Deprecated
    default public Float right() {
        return Float.valueOf(this.rightFloat());
    }

    default public FloatFloatPair right(float r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatFloatPair right(Float l) {
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

    default public FloatFloatPair second(float r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatFloatPair second(Float l) {
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

    default public FloatFloatPair value(float r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatFloatPair value(Float l) {
        return this.value(l.floatValue());
    }

    public static FloatFloatPair of(float left, float right) {
        return new FloatFloatImmutablePair(left, right);
    }

    public static Comparator<FloatFloatPair> lexComparator() {
        return (x, y) -> {
            int t = Float.compare(x.leftFloat(), y.leftFloat());
            if (t != 0) {
                return t;
            }
            return Float.compare(x.rightFloat(), y.rightFloat());
        };
    }
}

