/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanFloatImmutablePair;
import java.util.Comparator;

public interface BooleanFloatPair
extends Pair<Boolean, Float> {
    public boolean leftBoolean();

    @Override
    @Deprecated
    default public Boolean left() {
        return this.leftBoolean();
    }

    default public BooleanFloatPair left(boolean l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanFloatPair left(Boolean l) {
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

    default public BooleanFloatPair first(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanFloatPair first(Boolean l) {
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

    default public BooleanFloatPair key(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanFloatPair key(Boolean l) {
        return this.key((boolean)l);
    }

    public float rightFloat();

    @Override
    @Deprecated
    default public Float right() {
        return Float.valueOf(this.rightFloat());
    }

    default public BooleanFloatPair right(float r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanFloatPair right(Float l) {
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

    default public BooleanFloatPair second(float r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanFloatPair second(Float l) {
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

    default public BooleanFloatPair value(float r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanFloatPair value(Float l) {
        return this.value(l.floatValue());
    }

    public static BooleanFloatPair of(boolean left, float right) {
        return new BooleanFloatImmutablePair(left, right);
    }

    public static Comparator<BooleanFloatPair> lexComparator() {
        return (x, y) -> {
            int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
            if (t != 0) {
                return t;
            }
            return Float.compare(x.rightFloat(), y.rightFloat());
        };
    }
}

