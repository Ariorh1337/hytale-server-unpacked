/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharLongImmutablePair;
import java.util.Comparator;

public interface CharLongPair
extends Pair<Character, Long> {
    public char leftChar();

    @Override
    @Deprecated
    default public Character left() {
        return Character.valueOf(this.leftChar());
    }

    default public CharLongPair left(char l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharLongPair left(Character l) {
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

    default public CharLongPair first(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharLongPair first(Character l) {
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

    default public CharLongPair key(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharLongPair key(Character l) {
        return this.key(l.charValue());
    }

    public long rightLong();

    @Override
    @Deprecated
    default public Long right() {
        return this.rightLong();
    }

    default public CharLongPair right(long r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharLongPair right(Long l) {
        return this.right((long)l);
    }

    default public long secondLong() {
        return this.rightLong();
    }

    @Override
    @Deprecated
    default public Long second() {
        return this.secondLong();
    }

    default public CharLongPair second(long r) {
        return this.right(r);
    }

    @Deprecated
    default public CharLongPair second(Long l) {
        return this.second((long)l);
    }

    default public long valueLong() {
        return this.rightLong();
    }

    @Override
    @Deprecated
    default public Long value() {
        return this.valueLong();
    }

    default public CharLongPair value(long r) {
        return this.right(r);
    }

    @Deprecated
    default public CharLongPair value(Long l) {
        return this.value((long)l);
    }

    public static CharLongPair of(char left, long right) {
        return new CharLongImmutablePair(left, right);
    }

    public static Comparator<CharLongPair> lexComparator() {
        return (x, y) -> {
            int t = Character.compare(x.leftChar(), y.leftChar());
            if (t != 0) {
                return t;
            }
            return Long.compare(x.rightLong(), y.rightLong());
        };
    }
}

