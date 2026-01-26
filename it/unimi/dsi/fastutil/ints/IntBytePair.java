/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntByteImmutablePair;
import java.util.Comparator;

public interface IntBytePair
extends Pair<Integer, Byte> {
    public int leftInt();

    @Override
    @Deprecated
    default public Integer left() {
        return this.leftInt();
    }

    default public IntBytePair left(int l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntBytePair left(Integer l) {
        return this.left((int)l);
    }

    default public int firstInt() {
        return this.leftInt();
    }

    @Override
    @Deprecated
    default public Integer first() {
        return this.firstInt();
    }

    default public IntBytePair first(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntBytePair first(Integer l) {
        return this.first((int)l);
    }

    default public int keyInt() {
        return this.firstInt();
    }

    @Override
    @Deprecated
    default public Integer key() {
        return this.keyInt();
    }

    default public IntBytePair key(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntBytePair key(Integer l) {
        return this.key((int)l);
    }

    public byte rightByte();

    @Override
    @Deprecated
    default public Byte right() {
        return this.rightByte();
    }

    default public IntBytePair right(byte r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntBytePair right(Byte l) {
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

    default public IntBytePair second(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public IntBytePair second(Byte l) {
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

    default public IntBytePair value(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public IntBytePair value(Byte l) {
        return this.value((byte)l);
    }

    public static IntBytePair of(int left, byte right) {
        return new IntByteImmutablePair(left, right);
    }

    public static Comparator<IntBytePair> lexComparator() {
        return (x, y) -> {
            int t = Integer.compare(x.leftInt(), y.leftInt());
            if (t != 0) {
                return t;
            }
            return Byte.compare(x.rightByte(), y.rightByte());
        };
    }
}

