/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;

@FunctionalInterface
public interface ShortBinaryOperator
extends BinaryOperator<Short>,
IntBinaryOperator {
    @Override
    public short apply(short var1, short var2);

    @Override
    @Deprecated
    default public int applyAsInt(int x, int y) {
        return this.apply(SafeMath.safeIntToShort(x), SafeMath.safeIntToShort(y));
    }

    @Override
    @Deprecated
    default public Short apply(Short x, Short y) {
        return this.apply((short)x, (short)y);
    }
}

