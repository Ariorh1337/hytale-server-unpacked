/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import java.util.function.BinaryOperator;

@FunctionalInterface
public interface BooleanBinaryOperator
extends BinaryOperator<Boolean> {
    @Override
    public boolean apply(boolean var1, boolean var2);

    @Override
    @Deprecated
    default public Boolean apply(Boolean x, Boolean y) {
        return this.apply((boolean)x, (boolean)y);
    }
}

