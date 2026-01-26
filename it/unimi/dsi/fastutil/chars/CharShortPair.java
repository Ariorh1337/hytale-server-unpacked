/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharShortImmutablePair;
import java.util.Comparator;

public interface CharShortPair
extends Pair<Character, Short> {
    public char leftChar();

    @Override
    @Deprecated
    default public Character left() {
        return Character.valueOf(this.leftChar());
    }

    default public CharShortPair left(char l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharShortPair left(Character l) {
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

    default public CharShortPair first(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharShortPair first(Character l) {
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

    default public CharShortPair key(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharShortPair key(Character l) {
        return this.key(l.charValue());
    }

    public short rightShort();

    @Override
    @Deprecated
    default public Short right() {
        return this.rightShort();
    }

    default public CharShortPair right(short r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharShortPair right(Short l) {
        return this.right((short)l);
    }

    default public short secondShort() {
        return this.rightShort();
    }

    @Override
    @Deprecated
    default public Short second() {
        return this.secondShort();
    }

    default public CharShortPair second(short r) {
        return this.right(r);
    }

    @Deprecated
    default public CharShortPair second(Short l) {
        return this.second((short)l);
    }

    default public short valueShort() {
        return this.rightShort();
    }

    @Override
    @Deprecated
    default public Short value() {
        return this.valueShort();
    }

    default public CharShortPair value(short r) {
        return this.right(r);
    }

    @Deprecated
    default public CharShortPair value(Short l) {
        return this.value((short)l);
    }

    public static CharShortPair of(char left, short right) {
        return new CharShortImmutablePair(left, right);
    }

    public static Comparator<CharShortPair> lexComparator() {
        return (x, y) -> {
            int t = Character.compare(x.leftChar(), y.leftChar());
            if (t != 0) {
                return t;
            }
            return Short.compare(x.rightShort(), y.rightShort());
        };
    }
}

