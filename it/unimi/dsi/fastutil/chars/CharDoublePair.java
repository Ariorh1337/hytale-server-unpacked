/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharDoubleImmutablePair;
import java.util.Comparator;

public interface CharDoublePair
extends Pair<Character, Double> {
    public char leftChar();

    @Override
    @Deprecated
    default public Character left() {
        return Character.valueOf(this.leftChar());
    }

    default public CharDoublePair left(char l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharDoublePair left(Character l) {
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

    default public CharDoublePair first(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharDoublePair first(Character l) {
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

    default public CharDoublePair key(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharDoublePair key(Character l) {
        return this.key(l.charValue());
    }

    public double rightDouble();

    @Override
    @Deprecated
    default public Double right() {
        return this.rightDouble();
    }

    default public CharDoublePair right(double r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharDoublePair right(Double l) {
        return this.right((double)l);
    }

    default public double secondDouble() {
        return this.rightDouble();
    }

    @Override
    @Deprecated
    default public Double second() {
        return this.secondDouble();
    }

    default public CharDoublePair second(double r) {
        return this.right(r);
    }

    @Deprecated
    default public CharDoublePair second(Double l) {
        return this.second((double)l);
    }

    default public double valueDouble() {
        return this.rightDouble();
    }

    @Override
    @Deprecated
    default public Double value() {
        return this.valueDouble();
    }

    default public CharDoublePair value(double r) {
        return this.right(r);
    }

    @Deprecated
    default public CharDoublePair value(Double l) {
        return this.value((double)l);
    }

    public static CharDoublePair of(char left, double right) {
        return new CharDoubleImmutablePair(left, right);
    }

    public static Comparator<CharDoublePair> lexComparator() {
        return (x, y) -> {
            int t = Character.compare(x.leftChar(), y.leftChar());
            if (t != 0) {
                return t;
            }
            return Double.compare(x.rightDouble(), y.rightDouble());
        };
    }
}

