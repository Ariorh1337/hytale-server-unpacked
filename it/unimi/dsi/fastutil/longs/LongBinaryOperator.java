/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import java.util.function.BinaryOperator;

@FunctionalInterface
public interface LongBinaryOperator
extends BinaryOperator<Long>,
java.util.function.LongBinaryOperator {
    @Override
    public long apply(long var1, long var3);

    @Override
    @Deprecated
    default public long applyAsLong(long x, long y) {
        return this.apply(x, y);
    }

    @Override
    @Deprecated
    default public Long apply(Long x, Long y) {
        return this.apply((long)x, (long)y);
    }
}

