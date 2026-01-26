/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatShortImmutablePair;
import java.util.Comparator;

public interface FloatShortPair
extends Pair<Float, Short> {
    public float leftFloat();

    @Override
    @Deprecated
    default public Float left() {
        return Float.valueOf(this.leftFloat());
    }

    default public FloatShortPair left(float l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatShortPair left(Float l) {
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

    default public FloatShortPair first(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatShortPair first(Float l) {
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

    default public FloatShortPair key(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatShortPair key(Float l) {
        return this.key(l.floatValue());
    }

    public short rightShort();

    @Override
    @Deprecated
    default public Short right() {
        return this.rightShort();
    }

    default public FloatShortPair right(short r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatShortPair right(Short l) {
        return this.right((short)l);
    }

    default public short secondShort() {
        return this.rightShort();
    }

    @Override
    @Deprecated
    default public Short second() {
        return this.secondShort();
    }

    default public FloatShortPair second(short r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatShortPair second(Short l) {
        return this.second((short)l);
    }

    default public short valueShort() {
        return this.rightShort();
    }

    @Override
    @Deprecated
    default public Short value() {
        return this.valueShort();
    }

    default public FloatShortPair value(short r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatShortPair value(Short l) {
        return this.value((short)l);
    }

    public static FloatShortPair of(float left, short right) {
        return new FloatShortImmutablePair(left, right);
    }

    public static Comparator<FloatShortPair> lexComparator() {
        return (x, y) -> {
            int t = Float.compare(x.leftFloat(), y.leftFloat());
            if (t != 0) {
                return t;
            }
            return Short.compare(x.rightShort(), y.rightShort());
        };
    }
}

