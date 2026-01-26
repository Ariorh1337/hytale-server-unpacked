/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatBooleanImmutablePair;
import java.util.Comparator;

public interface FloatBooleanPair
extends Pair<Float, Boolean> {
    public float leftFloat();

    @Override
    @Deprecated
    default public Float left() {
        return Float.valueOf(this.leftFloat());
    }

    default public FloatBooleanPair left(float l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatBooleanPair left(Float l) {
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

    default public FloatBooleanPair first(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatBooleanPair first(Float l) {
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

    default public FloatBooleanPair key(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatBooleanPair key(Float l) {
        return this.key(l.floatValue());
    }

    public boolean rightBoolean();

    @Override
    @Deprecated
    default public Boolean right() {
        return this.rightBoolean();
    }

    default public FloatBooleanPair right(boolean r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatBooleanPair right(Boolean l) {
        return this.right((boolean)l);
    }

    default public boolean secondBoolean() {
        return this.rightBoolean();
    }

    @Override
    @Deprecated
    default public Boolean second() {
        return this.secondBoolean();
    }

    default public FloatBooleanPair second(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatBooleanPair second(Boolean l) {
        return this.second((boolean)l);
    }

    default public boolean valueBoolean() {
        return this.rightBoolean();
    }

    @Override
    @Deprecated
    default public Boolean value() {
        return this.valueBoolean();
    }

    default public FloatBooleanPair value(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatBooleanPair value(Boolean l) {
        return this.value((boolean)l);
    }

    public static FloatBooleanPair of(float left, boolean right) {
        return new FloatBooleanImmutablePair(left, right);
    }

    public static Comparator<FloatBooleanPair> lexComparator() {
        return (x, y) -> {
            int t = Float.compare(x.leftFloat(), y.leftFloat());
            if (t != 0) {
                return t;
            }
            return Boolean.compare(x.rightBoolean(), y.rightBoolean());
        };
    }
}

