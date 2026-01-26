/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntShortImmutablePair;
import java.util.Comparator;

public interface IntShortPair
extends Pair<Integer, Short> {
    public int leftInt();

    @Override
    @Deprecated
    default public Integer left() {
        return this.leftInt();
    }

    default public IntShortPair left(int l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntShortPair left(Integer l) {
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

    default public IntShortPair first(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntShortPair first(Integer l) {
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

    default public IntShortPair key(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntShortPair key(Integer l) {
        return this.key((int)l);
    }

    public short rightShort();

    @Override
    @Deprecated
    default public Short right() {
        return this.rightShort();
    }

    default public IntShortPair right(short r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntShortPair right(Short l) {
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

    default public IntShortPair second(short r) {
        return this.right(r);
    }

    @Deprecated
    default public IntShortPair second(Short l) {
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

    default public IntShortPair value(short r) {
        return this.right(r);
    }

    @Deprecated
    default public IntShortPair value(Short l) {
        return this.value((short)l);
    }

    public static IntShortPair of(int left, short right) {
        return new IntShortImmutablePair(left, right);
    }

    public static Comparator<IntShortPair> lexComparator() {
        return (x, y) -> {
            int t = Integer.compare(x.leftInt(), y.leftInt());
            if (t != 0) {
                return t;
            }
            return Short.compare(x.rightShort(), y.rightShort());
        };
    }
}

