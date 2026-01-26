/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharIntImmutablePair;
import java.util.Comparator;

public interface CharIntPair
extends Pair<Character, Integer> {
    public char leftChar();

    @Override
    @Deprecated
    default public Character left() {
        return Character.valueOf(this.leftChar());
    }

    default public CharIntPair left(char l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharIntPair left(Character l) {
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

    default public CharIntPair first(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharIntPair first(Character l) {
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

    default public CharIntPair key(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharIntPair key(Character l) {
        return this.key(l.charValue());
    }

    public int rightInt();

    @Override
    @Deprecated
    default public Integer right() {
        return this.rightInt();
    }

    default public CharIntPair right(int r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharIntPair right(Integer l) {
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

    default public CharIntPair second(int r) {
        return this.right(r);
    }

    @Deprecated
    default public CharIntPair second(Integer l) {
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

    default public CharIntPair value(int r) {
        return this.right(r);
    }

    @Deprecated
    default public CharIntPair value(Integer l) {
        return this.value((int)l);
    }

    public static CharIntPair of(char left, int right) {
        return new CharIntImmutablePair(left, right);
    }

    public static Comparator<CharIntPair> lexComparator() {
        return (x, y) -> {
            int t = Character.compare(x.leftChar(), y.leftChar());
            if (t != 0) {
                return t;
            }
            return Integer.compare(x.rightInt(), y.rightInt());
        };
    }
}

