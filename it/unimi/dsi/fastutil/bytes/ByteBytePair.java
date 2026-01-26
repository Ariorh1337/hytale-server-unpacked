/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteByteImmutablePair;
import java.util.Comparator;

public interface ByteBytePair
extends Pair<Byte, Byte> {
    public byte leftByte();

    @Override
    @Deprecated
    default public Byte left() {
        return this.leftByte();
    }

    default public ByteBytePair left(byte l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteBytePair left(Byte l) {
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

    default public ByteBytePair first(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteBytePair first(Byte l) {
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

    default public ByteBytePair key(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteBytePair key(Byte l) {
        return this.key((byte)l);
    }

    public byte rightByte();

    @Override
    @Deprecated
    default public Byte right() {
        return this.rightByte();
    }

    default public ByteBytePair right(byte r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteBytePair right(Byte l) {
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

    default public ByteBytePair second(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteBytePair second(Byte l) {
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

    default public ByteBytePair value(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteBytePair value(Byte l) {
        return this.value((byte)l);
    }

    public static ByteBytePair of(byte left, byte right) {
        return new ByteByteImmutablePair(left, right);
    }

    public static Comparator<ByteBytePair> lexComparator() {
        return (x, y) -> {
            int t = Byte.compare(x.leftByte(), y.leftByte());
            if (t != 0) {
                return t;
            }
            return Byte.compare(x.rightByte(), y.rightByte());
        };
    }
}

