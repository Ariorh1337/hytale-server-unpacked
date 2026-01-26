/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortFloatImmutablePair;
import java.util.Comparator;

public interface ShortFloatPair
extends Pair<Short, Float> {
    public short leftShort();

    @Override
    @Deprecated
    default public Short left() {
        return this.leftShort();
    }

    default public ShortFloatPair left(short l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortFloatPair left(Short l) {
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

    default public ShortFloatPair first(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortFloatPair first(Short l) {
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

    default public ShortFloatPair key(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortFloatPair key(Short l) {
        return this.key((short)l);
    }

    public float rightFloat();

    @Override
    @Deprecated
    default public Float right() {
        return Float.valueOf(this.rightFloat());
    }

    default public ShortFloatPair right(float r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortFloatPair right(Float l) {
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

    default public ShortFloatPair second(float r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortFloatPair second(Float l) {
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

    default public ShortFloatPair value(float r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortFloatPair value(Float l) {
        return this.value(l.floatValue());
    }

    public static ShortFloatPair of(short left, float right) {
        return new ShortFloatImmutablePair(left, right);
    }

    public static Comparator<ShortFloatPair> lexComparator() {
        return (x, y) -> {
            int t = Short.compare(x.leftShort(), y.leftShort());
            if (t != 0) {
                return t;
            }
            return Float.compare(x.rightFloat(), y.rightFloat());
        };
    }
}

