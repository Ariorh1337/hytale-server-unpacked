/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntFloatImmutablePair;
import java.util.Comparator;

public interface IntFloatPair
extends Pair<Integer, Float> {
    public int leftInt();

    @Override
    @Deprecated
    default public Integer left() {
        return this.leftInt();
    }

    default public IntFloatPair left(int l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntFloatPair left(Integer l) {
        return this.left((int)l);
    }

    default public int firstInt() {
        return this.leftInt();
    }

    @Override
    @Deprecated
    default public Integer first() {
        return this.firstInt();
    }

    default public IntFloatPair first(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntFloatPair first(Integer l) {
        return this.first((int)l);
    }

    default public int keyInt() {
        return this.firstInt();
    }

    @Override
    @Deprecated
    default public Integer key() {
        return this.keyInt();
    }

    default public IntFloatPair key(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntFloatPair key(Integer l) {
        return this.key((int)l);
    }

    public float rightFloat();

    @Override
    @Deprecated
    default public Float right() {
        return Float.valueOf(this.rightFloat());
    }

    default public IntFloatPair right(float r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntFloatPair right(Float l) {
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

    default public IntFloatPair second(float r) {
        return this.right(r);
    }

    @Deprecated
    default public IntFloatPair second(Float l) {
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

    default public IntFloatPair value(float r) {
        return this.right(r);
    }

    @Deprecated
    default public IntFloatPair value(Float l) {
        return this.value(l.floatValue());
    }

    public static IntFloatPair of(int left, float right) {
        return new IntFloatImmutablePair(left, right);
    }

    public static Comparator<IntFloatPair> lexComparator() {
        return (x, y) -> {
            int t = Integer.compare(x.leftInt(), y.leftInt());
            if (t != 0) {
                return t;
            }
            return Float.compare(x.rightFloat(), y.rightFloat());
        };
    }
}

