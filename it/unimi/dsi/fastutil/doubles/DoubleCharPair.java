/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleCharImmutablePair;
import java.util.Comparator;

public interface DoubleCharPair
extends Pair<Double, Character> {
    public double leftDouble();

    @Override
    @Deprecated
    default public Double left() {
        return this.leftDouble();
    }

    default public DoubleCharPair left(double l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleCharPair left(Double l) {
        return this.left((double)l);
    }

    default public double firstDouble() {
        return this.leftDouble();
    }

    @Override
    @Deprecated
    default public Double first() {
        return this.firstDouble();
    }

    default public DoubleCharPair first(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleCharPair first(Double l) {
        return this.first((double)l);
    }

    default public double keyDouble() {
        return this.firstDouble();
    }

    @Override
    @Deprecated
    default public Double key() {
        return this.keyDouble();
    }

    default public DoubleCharPair key(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleCharPair key(Double l) {
        return this.key((double)l);
    }

    public char rightChar();

    @Override
    @Deprecated
    default public Character right() {
        return Character.valueOf(this.rightChar());
    }

    default public DoubleCharPair right(char r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleCharPair right(Character l) {
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

    default public DoubleCharPair second(char r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleCharPair second(Character l) {
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

    default public DoubleCharPair value(char r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleCharPair value(Character l) {
        return this.value(l.charValue());
    }

    public static DoubleCharPair of(double left, char right) {
        return new DoubleCharImmutablePair(left, right);
    }

    public static Comparator<DoubleCharPair> lexComparator() {
        return (x, y) -> {
            int t = Double.compare(x.leftDouble(), y.leftDouble());
            if (t != 0) {
                return t;
            }
            return Character.compare(x.rightChar(), y.rightChar());
        };
    }
}

