/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortBooleanImmutablePair;
import java.util.Comparator;

public interface ShortBooleanPair
extends Pair<Short, Boolean> {
    public short leftShort();

    @Override
    @Deprecated
    default public Short left() {
        return this.leftShort();
    }

    default public ShortBooleanPair left(short l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortBooleanPair left(Short l) {
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

    default public ShortBooleanPair first(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortBooleanPair first(Short l) {
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

    default public ShortBooleanPair key(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortBooleanPair key(Short l) {
        return this.key((short)l);
    }

    public boolean rightBoolean();

    @Override
    @Deprecated
    default public Boolean right() {
        return this.rightBoolean();
    }

    default public ShortBooleanPair right(boolean r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortBooleanPair right(Boolean l) {
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

    default public ShortBooleanPair second(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortBooleanPair second(Boolean l) {
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

    default public ShortBooleanPair value(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public ShortBooleanPair value(Boolean l) {
        return this.value((boolean)l);
    }

    public static ShortBooleanPair of(short left, boolean right) {
        return new ShortBooleanImmutablePair(left, right);
    }

    public static Comparator<ShortBooleanPair> lexComparator() {
        return (x, y) -> {
            int t = Short.compare(x.leftShort(), y.leftShort());
            if (t != 0) {
                return t;
            }
            return Boolean.compare(x.rightBoolean(), y.rightBoolean());
        };
    }
}

