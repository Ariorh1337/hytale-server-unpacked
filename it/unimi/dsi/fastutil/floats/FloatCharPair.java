/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatCharImmutablePair;
import java.util.Comparator;

public interface FloatCharPair
extends Pair<Float, Character> {
    public float leftFloat();

    @Override
    @Deprecated
    default public Float left() {
        return Float.valueOf(this.leftFloat());
    }

    default public FloatCharPair left(float l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatCharPair left(Float l) {
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

    default public FloatCharPair first(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatCharPair first(Float l) {
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

    default public FloatCharPair key(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatCharPair key(Float l) {
        return this.key(l.floatValue());
    }

    public char rightChar();

    @Override
    @Deprecated
    default public Character right() {
        return Character.valueOf(this.rightChar());
    }

    default public FloatCharPair right(char r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatCharPair right(Character l) {
        return this.right(l.charValue());
    }

    default public char secondChar() {
        return this.rightChar();
    }

    @Override
    @Deprecated
    default public Character second() {
        return Character.valueOf(this.secondChar());
    }

    default public FloatCharPair second(char r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatCharPair second(Character l) {
        return this.second(l.charValue());
    }

    default public char valueChar() {
        return this.rightChar();
    }

    @Override
    @Deprecated
    default public Character value() {
        return Character.valueOf(this.valueChar());
    }

    default public FloatCharPair value(char r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatCharPair value(Character l) {
        return this.value(l.charValue());
    }

    public static FloatCharPair of(float left, char right) {
        return new FloatCharImmutablePair(left, right);
    }

    public static Comparator<FloatCharPair> lexComparator() {
        return (x, y) -> {
            int t = Float.compare(x.leftFloat(), y.leftFloat());
            if (t != 0) {
                return t;
            }
            return Character.compare(x.rightChar(), y.rightChar());
        };
    }
}

