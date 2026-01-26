/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongByteImmutablePair;
import java.util.Comparator;

public interface LongBytePair
extends Pair<Long, Byte> {
    public long leftLong();

    @Override
    @Deprecated
    default public Long left() {
        return this.leftLong();
    }

    default public LongBytePair left(long l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongBytePair left(Long l) {
        return this.left((long)l);
    }

    default public long firstLong() {
        return this.leftLong();
    }

    @Override
    @Deprecated
    default public Long first() {
        return this.firstLong();
    }

    default public LongBytePair first(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongBytePair first(Long l) {
        return this.first((long)l);
    }

    default public long keyLong() {
        return this.firstLong();
    }

    @Override
    @Deprecated
    default public Long key() {
        return this.keyLong();
    }

    default public LongBytePair key(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongBytePair key(Long l) {
        return this.key((long)l);
    }

    public byte rightByte();

    @Override
    @Deprecated
    default public Byte right() {
        return this.rightByte();
    }

    default public LongBytePair right(byte r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongBytePair right(Byte l) {
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

    default public LongBytePair second(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public LongBytePair second(Byte l) {
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

    default public LongBytePair value(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public LongBytePair value(Byte l) {
        return this.value((byte)l);
    }

    public static LongBytePair of(long left, byte right) {
        return new LongByteImmutablePair(left, right);
    }

    public static Comparator<LongBytePair> lexComparator() {
        return (x, y) -> {
            int t = Long.compare(x.leftLong(), y.leftLong());
            if (t != 0) {
                return t;
            }
            return Byte.compare(x.rightByte(), y.rightByte());
        };
    }
}

