/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteIntImmutablePair;
import java.util.Comparator;

public interface ByteIntPair
extends Pair<Byte, Integer> {
    public byte leftByte();

    @Override
    @Deprecated
    default public Byte left() {
        return this.leftByte();
    }

    default public ByteIntPair left(byte l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteIntPair left(Byte l) {
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

    default public ByteIntPair first(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteIntPair first(Byte l) {
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

    default public ByteIntPair key(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteIntPair key(Byte l) {
        return this.key((byte)l);
    }

    public int rightInt();

    @Override
    @Deprecated
    default public Integer right() {
        return this.rightInt();
    }

    default public ByteIntPair right(int r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteIntPair right(Integer l) {
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

    default public ByteIntPair second(int r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteIntPair second(Integer l) {
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

    default public ByteIntPair value(int r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteIntPair value(Integer l) {
        return this.value((int)l);
    }

    public static ByteIntPair of(byte left, int right) {
        return new ByteIntImmutablePair(left, right);
    }

    public static Comparator<ByteIntPair> lexComparator() {
        return (x, y) -> {
            int t = Byte.compare(x.leftByte(), y.leftByte());
            if (t != 0) {
                return t;
            }
            return Integer.compare(x.rightInt(), y.rightInt());
        };
    }
}

