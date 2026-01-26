/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharFloatImmutablePair;
import java.util.Comparator;

public interface CharFloatPair
extends Pair<Character, Float> {
    public char leftChar();

    @Override
    @Deprecated
    default public Character left() {
        return Character.valueOf(this.leftChar());
    }

    default public CharFloatPair left(char l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharFloatPair left(Character l) {
        return this.left(l.charValue());
    }

    default public char firstChar() {
        return this.leftChar();
    }

    @Override
    @Deprecated
    default public Character first() {
        return Character.valueOf(this.firstChar());
    }

    default public CharFloatPair first(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharFloatPair first(Character l) {
        return this.first(l.charValue());
    }

    default public char keyChar() {
        return this.firstChar();
    }

    @Override
    @Deprecated
    default public Character key() {
        return Character.valueOf(this.keyChar());
    }

    default public CharFloatPair key(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharFloatPair key(Character l) {
        return this.key(l.charValue());
    }

    public float rightFloat();

    @Override
    @Deprecated
    default public Float right() {
        return Float.valueOf(this.rightFloat());
    }

    default public CharFloatPair right(float r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharFloatPair right(Float l) {
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

    default public CharFloatPair second(float r) {
        return this.right(r);
    }

    @Deprecated
    default public CharFloatPair second(Float l) {
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

    default public CharFloatPair value(float r) {
        return this.right(r);
    }

    @Deprecated
    default public CharFloatPair value(Float l) {
        return this.value(l.floatValue());
    }

    public static CharFloatPair of(char left, float right) {
        return new CharFloatImmutablePair(left, right);
    }

    public static Comparator<CharFloatPair> lexComparator() {
        return (x, y) -> {
            int t = Character.compare(x.leftChar(), y.leftChar());
            if (t != 0) {
                return t;
            }
            return Float.compare(x.rightFloat(), y.rightFloat());
        };
    }
}

