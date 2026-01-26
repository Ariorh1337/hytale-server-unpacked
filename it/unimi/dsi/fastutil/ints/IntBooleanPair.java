/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntBooleanImmutablePair;
import java.util.Comparator;

public interface IntBooleanPair
extends Pair<Integer, Boolean> {
    public int leftInt();

    @Override
    @Deprecated
    default public Integer left() {
        return this.leftInt();
    }

    default public IntBooleanPair left(int l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntBooleanPair left(Integer l) {
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

    default public IntBooleanPair first(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntBooleanPair first(Integer l) {
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

    default public IntBooleanPair key(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntBooleanPair key(Integer l) {
        return this.key((int)l);
    }

    public boolean rightBoolean();

    @Override
    @Deprecated
    default public Boolean right() {
        return this.rightBoolean();
    }

    default public IntBooleanPair right(boolean r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntBooleanPair right(Boolean l) {
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

    default public IntBooleanPair second(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public IntBooleanPair second(Boolean l) {
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

    default public IntBooleanPair value(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public IntBooleanPair value(Boolean l) {
        return this.value((boolean)l);
    }

    public static IntBooleanPair of(int left, boolean right) {
        return new IntBooleanImmutablePair(left, right);
    }

    public static Comparator<IntBooleanPair> lexComparator() {
        return (x, y) -> {
            int t = Integer.compare(x.leftInt(), y.leftInt());
            if (t != 0) {
                return t;
            }
            return Boolean.compare(x.rightBoolean(), y.rightBoolean());
        };
    }
}

