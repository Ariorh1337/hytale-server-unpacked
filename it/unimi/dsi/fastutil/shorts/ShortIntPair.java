/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortIntImmutablePair;
import java.util.Comparator;

public interface ShortIntPair
extends Pair<Short, Integer> {
    public short leftShort();

    @Override
    @Deprecated
    default public Short left() {
        return this.leftShort();
    }

    default public ShortIntPair left(short l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortIntPair left(Short l) {
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

    default public ShortIntPair first(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortIntPair first(Short l) {
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

    default public ShortIntPair key(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortIntPair key(Short l) {
        return this.key((short)l);
    }

    public int rightInt();

    @Override
    @Deprecated
    default public Integer right() {
        return this.rightInt();
    }

    default public ShortIntPair right(int r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortIntPair right(Integer l) {
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

    default public ShortIntPair second(int r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortIntPair second(Integer l) {
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

    default public ShortIntPair value(int r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortIntPair value(Integer l) {
        return this.value((int)l);
    }

    public static ShortIntPair of(short left, int right) {
        return new ShortIntImmutablePair(left, right);
    }

    public static Comparator<ShortIntPair> lexComparator() {
        return (x, y) -> {
            int t = Short.compare(x.leftShort(), y.leftShort());
            if (t != 0) {
                return t;
            }
            return Integer.compare(x.rightInt(), y.rightInt());
        };
    }
}

