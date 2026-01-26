/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatIntImmutablePair;
import java.util.Comparator;

public interface FloatIntPair
extends Pair<Float, Integer> {
    public float leftFloat();

    @Override
    @Deprecated
    default public Float left() {
        return Float.valueOf(this.leftFloat());
    }

    default public FloatIntPair left(float l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatIntPair left(Float l) {
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

    default public FloatIntPair first(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatIntPair first(Float l) {
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

    default public FloatIntPair key(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatIntPair key(Float l) {
        return this.key(l.floatValue());
    }

    public int rightInt();

    @Override
    @Deprecated
    default public Integer right() {
        return this.rightInt();
    }

    default public FloatIntPair right(int r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatIntPair right(Integer l) {
        return this.right((int)l);
    }

    default public int secondInt() {
        return this.rightInt();
    }

    @Override
    @Deprecated
    default public Integer second() {
        return this.secondInt();
    }

    default public FloatIntPair second(int r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatIntPair second(Integer l) {
        return this.second((int)l);
    }

    default public int valueInt() {
        return this.rightInt();
    }

    @Override
    @Deprecated
    default public Integer value() {
        return this.valueInt();
    }

    default public FloatIntPair value(int r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatIntPair value(Integer l) {
        return this.value((int)l);
    }

    public static FloatIntPair of(float left, int right) {
        return new FloatIntImmutablePair(left, right);
    }

    public static Comparator<FloatIntPair> lexComparator() {
        return (x, y) -> {
            int t = Float.compare(x.leftFloat(), y.leftFloat());
            if (t != 0) {
                return t;
            }
            return Integer.compare(x.rightInt(), y.rightInt());
        };
    }
}

