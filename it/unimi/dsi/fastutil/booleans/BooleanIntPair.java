/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanIntImmutablePair;
import java.util.Comparator;

public interface BooleanIntPair
extends Pair<Boolean, Integer> {
    public boolean leftBoolean();

    @Override
    @Deprecated
    default public Boolean left() {
        return this.leftBoolean();
    }

    default public BooleanIntPair left(boolean l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanIntPair left(Boolean l) {
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

    default public BooleanIntPair first(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanIntPair first(Boolean l) {
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

    default public BooleanIntPair key(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanIntPair key(Boolean l) {
        return this.key((boolean)l);
    }

    public int rightInt();

    @Override
    @Deprecated
    default public Integer right() {
        return this.rightInt();
    }

    default public BooleanIntPair right(int r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanIntPair right(Integer l) {
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

    default public BooleanIntPair second(int r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanIntPair second(Integer l) {
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

    default public BooleanIntPair value(int r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanIntPair value(Integer l) {
        return this.value((int)l);
    }

    public static BooleanIntPair of(boolean left, int right) {
        return new BooleanIntImmutablePair(left, right);
    }

    public static Comparator<BooleanIntPair> lexComparator() {
        return (x, y) -> {
            int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
            if (t != 0) {
                return t;
            }
            return Integer.compare(x.rightInt(), y.rightInt());
        };
    }
}

