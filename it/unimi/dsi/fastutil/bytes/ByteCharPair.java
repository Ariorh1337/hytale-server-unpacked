/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteCharImmutablePair;
import java.util.Comparator;

public interface ByteCharPair
extends Pair<Byte, Character> {
    public byte leftByte();

    @Override
    @Deprecated
    default public Byte left() {
        return this.leftByte();
    }

    default public ByteCharPair left(byte l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteCharPair left(Byte l) {
        return this.left((byte)l);
    }

    default public byte firstByte() {
        return this.leftByte();
    }

    @Override
    @Deprecated
    default public Byte first() {
        return this.firstByte();
    }

    default public ByteCharPair first(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteCharPair first(Byte l) {
        return this.first((byte)l);
    }

    default public byte keyByte() {
        return this.firstByte();
    }

    @Override
    @Deprecated
    default public Byte key() {
        return this.keyByte();
    }

    default public ByteCharPair key(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteCharPair key(Byte l) {
        return this.key((byte)l);
    }

    public char rightChar();

    @Override
    @Deprecated
    default public Character right() {
        return Character.valueOf(this.rightChar());
    }

    default public ByteCharPair right(char r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteCharPair right(Character l) {
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

    default public ByteCharPair second(char r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteCharPair second(Character l) {
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

    default public ByteCharPair value(char r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteCharPair value(Character l) {
        return this.value(l.charValue());
    }

    public static ByteCharPair of(byte left, char right) {
        return new ByteCharImmutablePair(left, right);
    }

    public static Comparator<ByteCharPair> lexComparator() {
        return (x, y) -> {
            int t = Byte.compare(x.leftByte(), y.leftByte());
            if (t != 0) {
                return t;
            }
            return Character.compare(x.rightChar(), y.rightChar());
        };
    }
}

