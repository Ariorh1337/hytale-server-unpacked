/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanShortImmutablePair;
import java.util.Comparator;

public interface BooleanShortPair
extends Pair<Boolean, Short> {
    public boolean leftBoolean();

    @Override
    @Deprecated
    default public Boolean left() {
        return this.leftBoolean();
    }

    default public BooleanShortPair left(boolean l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanShortPair left(Boolean l) {
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

    default public BooleanShortPair first(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanShortPair first(Boolean l) {
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

    default public BooleanShortPair key(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanShortPair key(Boolean l) {
        return this.key((boolean)l);
    }

    public short rightShort();

    @Override
    @Deprecated
    default public Short right() {
        return this.rightShort();
    }

    default public BooleanShortPair right(short r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanShortPair right(Short l) {
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

    default public BooleanShortPair second(short r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanShortPair second(Short l) {
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

    default public BooleanShortPair value(short r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanShortPair value(Short l) {
        return this.value((short)l);
    }

    public static BooleanShortPair of(boolean left, short right) {
        return new BooleanShortImmutablePair(left, right);
    }

    public static Comparator<BooleanShortPair> lexComparator() {
        return (x, y) -> {
            int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
            if (t != 0) {
                return t;
            }
            return Short.compare(x.rightShort(), y.rightShort());
        };
    }
}

