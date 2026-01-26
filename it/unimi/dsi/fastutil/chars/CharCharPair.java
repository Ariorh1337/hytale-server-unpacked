/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharCharImmutablePair;
import java.util.Comparator;

public interface CharCharPair
extends Pair<Character, Character> {
    public char leftChar();

    @Override
    @Deprecated
    default public Character left() {
        return Character.valueOf(this.leftChar());
    }

    default public CharCharPair left(char l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharCharPair left(Character l) {
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

    default public CharCharPair first(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharCharPair first(Character l) {
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

    default public CharCharPair key(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharCharPair key(Character l) {
        return this.key(l.charValue());
    }

    public char rightChar();

    @Override
    @Deprecated
    default public Character right() {
        return Character.valueOf(this.rightChar());
    }

    default public CharCharPair right(char r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharCharPair right(Character l) {
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

    default public CharCharPair second(char r) {
        return this.right(r);
    }

    @Deprecated
    default public CharCharPair second(Character l) {
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

    default public CharCharPair value(char r) {
        return this.right(r);
    }

    @Deprecated
    default public CharCharPair value(Character l) {
        return this.value(l.charValue());
    }

    public static CharCharPair of(char left, char right) {
        return new CharCharImmutablePair(left, right);
    }

    public static Comparator<CharCharPair> lexComparator() {
        return (x, y) -> {
            int t = Character.compare(x.leftChar(), y.leftChar());
            if (t != 0) {
                return t;
            }
            return Character.compare(x.rightChar(), y.rightChar());
        };
    }
}

