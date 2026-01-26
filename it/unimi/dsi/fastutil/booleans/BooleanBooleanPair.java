/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanBooleanImmutablePair;
import java.util.Comparator;

public interface BooleanBooleanPair
extends Pair<Boolean, Boolean> {
    public boolean leftBoolean();

    @Override
    @Deprecated
    default public Boolean left() {
        return this.leftBoolean();
    }

    default public BooleanBooleanPair left(boolean l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanBooleanPair left(Boolean l) {
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

    default public BooleanBooleanPair first(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanBooleanPair first(Boolean l) {
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

    default public BooleanBooleanPair key(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanBooleanPair key(Boolean l) {
        return this.key((boolean)l);
    }

    public boolean rightBoolean();

    @Override
    @Deprecated
    default public Boolean right() {
        return this.rightBoolean();
    }

    default public BooleanBooleanPair right(boolean r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanBooleanPair right(Boolean l) {
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

    default public BooleanBooleanPair second(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanBooleanPair second(Boolean l) {
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

    default public BooleanBooleanPair value(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanBooleanPair value(Boolean l) {
        return this.value((boolean)l);
    }

    public static BooleanBooleanPair of(boolean left, boolean right) {
        return new BooleanBooleanImmutablePair(left, right);
    }

    public static Comparator<BooleanBooleanPair> lexComparator() {
        return (x, y) -> {
            int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
            if (t != 0) {
                return t;
            }
            return Boolean.compare(x.rightBoolean(), y.rightBoolean());
        };
    }
}

