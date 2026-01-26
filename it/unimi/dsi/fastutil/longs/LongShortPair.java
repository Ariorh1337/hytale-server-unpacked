/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongShortImmutablePair;
import java.util.Comparator;

public interface LongShortPair
extends Pair<Long, Short> {
    public long leftLong();

    @Override
    @Deprecated
    default public Long left() {
        return this.leftLong();
    }

    default public LongShortPair left(long l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongShortPair left(Long l) {
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

    default public LongShortPair first(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongShortPair first(Long l) {
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

    default public LongShortPair key(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongShortPair key(Long l) {
        return this.key((long)l);
    }

    public short rightShort();

    @Override
    @Deprecated
    default public Short right() {
        return this.rightShort();
    }

    default public LongShortPair right(short r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongShortPair right(Short l) {
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

    default public LongShortPair second(short r) {
        return this.right(r);
    }

    @Deprecated
    default public LongShortPair second(Short l) {
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

    default public LongShortPair value(short r) {
        return this.right(r);
    }

    @Deprecated
    default public LongShortPair value(Short l) {
        return this.value((short)l);
    }

    public static LongShortPair of(long left, short right) {
        return new LongShortImmutablePair(left, right);
    }

    public static Comparator<LongShortPair> lexComparator() {
        return (x, y) -> {
            int t = Long.compare(x.leftLong(), y.leftLong());
            if (t != 0) {
                return t;
            }
            return Short.compare(x.rightShort(), y.rightShort());
        };
    }
}

