/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface BooleanUnaryOperator
extends UnaryOperator<Boolean> {
    @Override
    public boolean apply(boolean var1);

    public static BooleanUnaryOperator identity() {
        return i -> i;
    }

    public static BooleanUnaryOperator negation() {
        return i -> !i;
    }

    @Override
    @Deprecated
    default public Boolean apply(Boolean x) {
        return this.apply((boolean)x);
    }
}

