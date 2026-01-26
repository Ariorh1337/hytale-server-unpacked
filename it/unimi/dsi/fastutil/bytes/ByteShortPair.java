/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteShortImmutablePair;
import java.util.Comparator;

public interface ByteShortPair
extends Pair<Byte, Short> {
    public byte leftByte();

    @Override
    @Deprecated
    default public Byte left() {
        return this.leftByte();
    }

    default public ByteShortPair left(byte l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteShortPair left(Byte l) {
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

    default public ByteShortPair first(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteShortPair first(Byte l) {
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

    default public ByteShortPair key(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteShortPair key(Byte l) {
        return this.key((byte)l);
    }

    public short rightShort();

    @Override
    @Deprecated
    default public Short right() {
        return this.rightShort();
    }

    default public ByteShortPair right(short r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteShortPair right(Short l) {
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

    default public ByteShortPair second(short r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteShortPair second(Short l) {
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

    default public ByteShortPair value(short r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteShortPair value(Short l) {
        return this.value((short)l);
    }

    public static ByteShortPair of(byte left, short right) {
        return new ByteShortImmutablePair(left, right);
    }

    public static Comparator<ByteShortPair> lexComparator() {
        return (x, y) -> {
            int t = Byte.compare(x.leftByte(), y.leftByte());
            if (t != 0) {
                return t;
            }
            return Short.compare(x.rightShort(), y.rightShort());
        };
    }
}

