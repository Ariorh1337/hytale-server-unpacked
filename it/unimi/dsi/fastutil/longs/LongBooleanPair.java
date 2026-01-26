/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongBooleanImmutablePair;
import java.util.Comparator;

public interface LongBooleanPair
extends Pair<Long, Boolean> {
    public long leftLong();

    @Override
    @Deprecated
    default public Long left() {
        return this.leftLong();
    }

    default public LongBooleanPair left(long l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongBooleanPair left(Long l) {
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

    default public LongBooleanPair first(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongBooleanPair first(Long l) {
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

    default public LongBooleanPair key(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongBooleanPair key(Long l) {
        return this.key((long)l);
    }

    public boolean rightBoolean();

    @Override
    @Deprecated
    default public Boolean right() {
        return this.rightBoolean();
    }

    default public LongBooleanPair right(boolean r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongBooleanPair right(Boolean l) {
        return this.right((boolean)l);
    }

    default public boolean secondBoolean() {
        return this.rightBoolean();
    }

    @Override
    @Deprecated
    default public Boolean second() {
        return this.secondBoolean();
    }

    default public LongBooleanPair second(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public LongBooleanPair second(Boolean l) {
        return this.second((boolean)l);
    }

    default public boolean valueBoolean() {
        return this.rightBoolean();
    }

    @Override
    @Deprecated
    default public Boolean value() {
        return this.valueBoolean();
    }

    default public LongBooleanPair value(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public LongBooleanPair value(Boolean l) {
        return this.value((boolean)l);
    }

    public static LongBooleanPair of(long left, boolean right) {
        return new LongBooleanImmutablePair(left, right);
    }

    public static Comparator<LongBooleanPair> lexComparator() {
        return (x, y) -> {
            int t = Long.compare(x.leftLong(), y.leftLong());
            if (t != 0) {
                return t;
            }
            return Boolean.compare(x.rightBoolean(), y.rightBoolean());
        };
    }
}

