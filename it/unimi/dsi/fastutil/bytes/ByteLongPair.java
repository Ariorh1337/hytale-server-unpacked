/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteLongImmutablePair;
import java.util.Comparator;

public interface ByteLongPair
extends Pair<Byte, Long> {
    public byte leftByte();

    @Override
    @Deprecated
    default public Byte left() {
        return this.leftByte();
    }

    default public ByteLongPair left(byte l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteLongPair left(Byte l) {
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

    default public ByteLongPair first(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteLongPair first(Byte l) {
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

    default public ByteLongPair key(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteLongPair key(Byte l) {
        return this.key((byte)l);
    }

    public long rightLong();

    @Override
    @Deprecated
    default public Long right() {
        return this.rightLong();
    }

    default public ByteLongPair right(long r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteLongPair right(Long l) {
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

    default public ByteLongPair second(long r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteLongPair second(Long l) {
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

    default public ByteLongPair value(long r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteLongPair value(Long l) {
        return this.value((long)l);
    }

    public static ByteLongPair of(byte left, long right) {
        return new ByteLongImmutablePair(left, right);
    }

    public static Comparator<ByteLongPair> lexComparator() {
        return (x, y) -> {
            int t = Byte.compare(x.leftByte(), y.leftByte());
            if (t != 0) {
                return t;
            }
            return Long.compare(x.rightLong(), y.rightLong());
        };
    }
}

