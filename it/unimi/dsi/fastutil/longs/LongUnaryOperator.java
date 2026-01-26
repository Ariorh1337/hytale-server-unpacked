/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface LongUnaryOperator
extends UnaryOperator<Long>,
java.util.function.LongUnaryOperator {
    @Override
    public long apply(long var1);

    public static LongUnaryOperator identity() {
        return i -> i;
    }

    public static LongUnaryOperator negation() {
        return i -> -i;
    }

    @Override
    @Deprecated
    default public long applyAsLong(long x) {
        return this.apply(x);
    }

    @Override
    @Deprecated
    default public Long apply(Long x) {
        return this.apply((long)x);
    }
}

