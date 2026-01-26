/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface ShortUnaryOperator
extends UnaryOperator<Short>,
IntUnaryOperator {
    @Override
    public short apply(short var1);

    public static ShortUnaryOperator identity() {
        return i -> i;
    }

    public static ShortUnaryOperator negation() {
        return i -> -i;
    }

    @Override
    @Deprecated
    default public int applyAsInt(int x) {
        return this.apply(SafeMath.safeIntToShort(x));
    }

    @Override
    @Deprecated
    default public Short apply(Short x) {
        return this.apply((short)x);
    }
}

