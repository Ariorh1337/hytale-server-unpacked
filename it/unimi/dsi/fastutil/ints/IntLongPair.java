/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntLongImmutablePair;
import java.util.Comparator;

public interface IntLongPair
extends Pair<Integer, Long> {
    public int leftInt();

    @Override
    @Deprecated
    default public Integer left() {
        return this.leftInt();
    }

    default public IntLongPair left(int l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntLongPair left(Integer l) {
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

    default public IntLongPair first(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntLongPair first(Integer l) {
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

    default public IntLongPair key(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntLongPair key(Integer l) {
        return this.key((int)l);
    }

    public long rightLong();

    @Override
    @Deprecated
    default public Long right() {
        return this.rightLong();
    }

    default public IntLongPair right(long r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntLongPair right(Long l) {
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

    default public IntLongPair second(long r) {
        return this.right(r);
    }

    @Deprecated
    default public IntLongPair second(Long l) {
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

    default public IntLongPair value(long r) {
        return this.right(r);
    }

    @Deprecated
    default public IntLongPair value(Long l) {
        return this.value((long)l);
    }

    public static IntLongPair of(int left, long right) {
        return new IntLongImmutablePair(left, right);
    }

    public static Comparator<IntLongPair> lexComparator() {
        return (x, y) -> {
            int t = Integer.compare(x.leftInt(), y.leftInt());
            if (t != 0) {
                return t;
            }
            return Long.compare(x.rightLong(), y.rightLong());
        };
    }
}

