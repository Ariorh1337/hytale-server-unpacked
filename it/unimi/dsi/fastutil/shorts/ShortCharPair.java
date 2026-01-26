/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortCharImmutablePair;
import java.util.Comparator;

public interface ShortCharPair
extends Pair<Short, Character> {
    public short leftShort();

    @Override
    @Deprecated
    default public Short left() {
        return this.leftShort();
    }

    default public ShortCharPair left(short l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortCharPair left(Short l) {
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

    default public ShortCharPair first(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortCharPair first(Short l) {
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

    default public ShortCharPair key(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortCharPair key(Short l) {
        return this.key((short)l);
    }

    public char rightChar();

    @Override
    @Deprecated
    default public Character right() {
        return Character.valueOf(this.rightChar());
    }

    default public ShortCharPair right(char r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortCharPair right(Character l) {
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

    default public ShortCharPair second(char r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortCharPair second(Character l) {
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

    default public ShortCharPair value(char r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortCharPair value(Character l) {
        return this.value(l.charValue());
    }

    public static ShortCharPair of(short left, char right) {
        return new ShortCharImmutablePair(left, right);
    }

    public static Comparator<ShortCharPair> lexComparator() {
        return (x, y) -> {
            int t = Short.compare(x.leftShort(), y.leftShort());
            if (t != 0) {
                return t;
            }
            return Character.compare(x.rightChar(), y.rightChar());
        };
    }
}

