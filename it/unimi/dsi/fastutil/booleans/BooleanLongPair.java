/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanLongImmutablePair;
import java.util.Comparator;

public interface BooleanLongPair
extends Pair<Boolean, Long> {
    public boolean leftBoolean();

    @Override
    @Deprecated
    default public Boolean left() {
        return this.leftBoolean();
    }

    default public BooleanLongPair left(boolean l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanLongPair left(Boolean l) {
        return this.left((boolean)l);
    }

    default public boolean firstBoolean() {
        return this.leftBoolean();
    }

    @Override
    @Deprecated
    default public Boolean first() {
        return this.firstBoolean();
    }

    default public BooleanLongPair first(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanLongPair first(Boolean l) {
        return this.first((boolean)l);
    }

    default public boolean keyBoolean() {
        return this.firstBoolean();
    }

    @Override
    @Deprecated
    default public Boolean key() {
        return this.keyBoolean();
    }

    default public BooleanLongPair key(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanLongPair key(Boolean l) {
        return this.key((boolean)l);
    }

    public long rightLong();

    @Override
    @Deprecated
    default public Long right() {
        return this.rightLong();
    }

    default public BooleanLongPair right(long r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanLongPair right(Long l) {
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

    default public BooleanLongPair second(long r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanLongPair second(Long l) {
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

    default public BooleanLongPair value(long r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanLongPair value(Long l) {
        return this.value((long)l);
    }

    public static BooleanLongPair of(boolean left, long right) {
        return new BooleanLongImmutablePair(left, right);
    }

    public static Comparator<BooleanLongPair> lexComparator() {
        return (x, y) -> {
            int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
            if (t != 0) {
                return t;
            }
            return Long.compare(x.rightLong(), y.rightLong());
        };
    }
}

