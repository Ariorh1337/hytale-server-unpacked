/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharByteImmutablePair;
import java.util.Comparator;

public interface CharBytePair
extends Pair<Character, Byte> {
    public char leftChar();

    @Override
    @Deprecated
    default public Character left() {
        return Character.valueOf(this.leftChar());
    }

    default public CharBytePair left(char l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharBytePair left(Character l) {
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

    default public CharBytePair first(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharBytePair first(Character l) {
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

    default public CharBytePair key(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharBytePair key(Character l) {
        return this.key(l.charValue());
    }

    public byte rightByte();

    @Override
    @Deprecated
    default public Byte right() {
        return this.rightByte();
    }

    default public CharBytePair right(byte r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharBytePair right(Byte l) {
        return this.right((byte)l);
    }

    default public byte secondByte() {
        return this.rightByte();
    }

    @Override
    @Deprecated
    default public Byte second() {
        return this.secondByte();
    }

    default public CharBytePair second(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public CharBytePair second(Byte l) {
        return this.second((byte)l);
    }

    default public byte valueByte() {
        return this.rightByte();
    }

    @Override
    @Deprecated
    default public Byte value() {
        return this.valueByte();
    }

    default public CharBytePair value(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public CharBytePair value(Byte l) {
        return this.value((byte)l);
    }

    public static CharBytePair of(char left, byte right) {
        return new CharByteImmutablePair(left, right);
    }

    public static Comparator<CharBytePair> lexComparator() {
        return (x, y) -> {
            int t = Character.compare(x.leftChar(), y.leftChar());
            if (t != 0) {
                return t;
            }
            return Byte.compare(x.rightByte(), y.rightByte());
        };
    }
}

