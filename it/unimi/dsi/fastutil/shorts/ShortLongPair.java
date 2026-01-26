/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortLongImmutablePair;
import java.util.Comparator;

public interface ShortLongPair
extends Pair<Short, Long> {
    public short leftShort();

    @Override
    @Deprecated
    default public Short left() {
        return this.leftShort();
    }

    default public ShortLongPair left(short l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortLongPair left(Short l) {
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

    default public ShortLongPair first(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortLongPair first(Short l) {
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

    default public ShortLongPair key(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortLongPair key(Short l) {
        return this.key((short)l);
    }

    public long rightLong();

    @Override
    @Deprecated
    default public Long right() {
        return this.rightLong();
    }

    default public ShortLongPair right(long r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortLongPair right(Long l) {
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

    default public ShortLongPair second(long r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortLongPair second(Long l) {
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

    default public ShortLongPair value(long r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortLongPair value(Long l) {
        return this.value((long)l);
    }

    public static ShortLongPair of(short left, long right) {
        return new ShortLongImmutablePair(left, right);
    }

    public static Comparator<ShortLongPair> lexComparator() {
        return (x, y) -> {
            int t = Short.compare(x.leftShort(), y.leftShort());
            if (t != 0) {
                return t;
            }
            return Long.compare(x.rightLong(), y.rightLong());
        };
    }
}

