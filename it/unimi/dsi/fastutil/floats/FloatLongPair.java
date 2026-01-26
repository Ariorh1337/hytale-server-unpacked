/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatLongImmutablePair;
import java.util.Comparator;

public interface FloatLongPair
extends Pair<Float, Long> {
    public float leftFloat();

    @Override
    @Deprecated
    default public Float left() {
        return Float.valueOf(this.leftFloat());
    }

    default public FloatLongPair left(float l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatLongPair left(Float l) {
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

    default public FloatLongPair first(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatLongPair first(Float l) {
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

    default public FloatLongPair key(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatLongPair key(Float l) {
        return this.key(l.floatValue());
    }

    public long rightLong();

    @Override
    @Deprecated
    default public Long right() {
        return this.rightLong();
    }

    default public FloatLongPair right(long r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatLongPair right(Long l) {
        return this.right((long)l);
    }

    default public long secondLong() {
        return this.rightLong();
    }

    @Override
    @Deprecated
    default public Long second() {
        return this.secondLong();
    }

    default public FloatLongPair second(long r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatLongPair second(Long l) {
        return this.second((long)l);
    }

    default public long valueLong() {
        return this.rightLong();
    }

    @Override
    @Deprecated
    default public Long value() {
        return this.valueLong();
    }

    default public FloatLongPair value(long r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatLongPair value(Long l) {
        return this.value((long)l);
    }

    public static FloatLongPair of(float left, long right) {
        return new FloatLongImmutablePair(left, right);
    }

    public static Comparator<FloatLongPair> lexComparator() {
        return (x, y) -> {
            int t = Float.compare(x.leftFloat(), y.leftFloat());
            if (t != 0) {
                return t;
            }
            return Long.compare(x.rightLong(), y.rightLong());
        };
    }
}

