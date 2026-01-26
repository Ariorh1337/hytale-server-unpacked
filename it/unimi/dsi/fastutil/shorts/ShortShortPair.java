/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortShortImmutablePair;
import java.util.Comparator;

public interface ShortShortPair
extends Pair<Short, Short> {
    public short leftShort();

    @Override
    @Deprecated
    default public Short left() {
        return this.leftShort();
    }

    default public ShortShortPair left(short l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortShortPair left(Short l) {
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

    default public ShortShortPair first(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortShortPair first(Short l) {
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

    default public ShortShortPair key(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortShortPair key(Short l) {
        return this.key((short)l);
    }

    public short rightShort();

    @Override
    @Deprecated
    default public Short right() {
        return this.rightShort();
    }

    default public ShortShortPair right(short r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortShortPair right(Short l) {
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

    default public ShortShortPair second(short r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortShortPair second(Short l) {
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

    default public ShortShortPair value(short r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortShortPair value(Short l) {
        return this.value((short)l);
    }

    public static ShortShortPair of(short left, short right) {
        return new ShortShortImmutablePair(left, right);
    }

    public static Comparator<ShortShortPair> lexComparator() {
        return (x, y) -> {
            int t = Short.compare(x.leftShort(), y.leftShort());
            if (t != 0) {
                return t;
            }
            return Short.compare(x.rightShort(), y.rightShort());
        };
    }
}

