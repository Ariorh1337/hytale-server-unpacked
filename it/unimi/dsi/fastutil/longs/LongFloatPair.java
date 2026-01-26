/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongFloatImmutablePair;
import java.util.Comparator;

public interface LongFloatPair
extends Pair<Long, Float> {
    public long leftLong();

    @Override
    @Deprecated
    default public Long left() {
        return this.leftLong();
    }

    default public LongFloatPair left(long l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongFloatPair left(Long l) {
        return this.left((long)l);
    }

    default public long firstLong() {
        return this.leftLong();
    }

    @Override
    @Deprecated
    default public Long first() {
        return this.firstLong();
    }

    default public LongFloatPair first(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongFloatPair first(Long l) {
        return this.first((long)l);
    }

    default public long keyLong() {
        return this.firstLong();
    }

    @Override
    @Deprecated
    default public Long key() {
        return this.keyLong();
    }

    default public LongFloatPair key(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongFloatPair key(Long l) {
        return this.key((long)l);
    }

    public float rightFloat();

    @Override
    @Deprecated
    default public Float right() {
        return Float.valueOf(this.rightFloat());
    }

    default public LongFloatPair right(float r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongFloatPair right(Float l) {
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

    default public LongFloatPair second(float r) {
        return this.right(r);
    }

    @Deprecated
    default public LongFloatPair second(Float l) {
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

    default public LongFloatPair value(float r) {
        return this.right(r);
    }

    @Deprecated
    default public LongFloatPair value(Float l) {
        return this.value(l.floatValue());
    }

    public static LongFloatPair of(long left, float right) {
        return new LongFloatImmutablePair(left, right);
    }

    public static Comparator<LongFloatPair> lexComparator() {
        return (x, y) -> {
            int t = Long.compare(x.leftLong(), y.leftLong());
            if (t != 0) {
                return t;
            }
            return Float.compare(x.rightFloat(), y.rightFloat());
        };
    }
}

