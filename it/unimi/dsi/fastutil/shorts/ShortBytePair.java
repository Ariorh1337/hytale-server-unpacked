/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortByteImmutablePair;
import java.util.Comparator;

public interface ShortBytePair
extends Pair<Short, Byte> {
    public short leftShort();

    @Override
    @Deprecated
    default public Short left() {
        return this.leftShort();
    }

    default public ShortBytePair left(short l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortBytePair left(Short l) {
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

    default public ShortBytePair first(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortBytePair first(Short l) {
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

    default public ShortBytePair key(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortBytePair key(Short l) {
        return this.key((short)l);
    }

    public byte rightByte();

    @Override
    @Deprecated
    default public Byte right() {
        return this.rightByte();
    }

    default public ShortBytePair right(byte r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortBytePair right(Byte l) {
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

    default public ShortBytePair second(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortBytePair second(Byte l) {
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

    default public ShortBytePair value(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortBytePair value(Byte l) {
        return this.value((byte)l);
    }

    public static ShortBytePair of(short left, byte right) {
        return new ShortByteImmutablePair(left, right);
    }

    public static Comparator<ShortBytePair> lexComparator() {
        return (x, y) -> {
            int t = Short.compare(x.leftShort(), y.leftShort());
            if (t != 0) {
                return t;
            }
            return Byte.compare(x.rightByte(), y.rightByte());
        };
    }
}

