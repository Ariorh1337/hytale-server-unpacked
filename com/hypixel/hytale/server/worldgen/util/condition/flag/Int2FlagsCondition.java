/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.util.condition.flag;

import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Int2FlagsCondition
extends IntUnaryOperator {
    public int eval(int var1);

    @Override
    default public int applyAsInt(int operand) {
        return this.eval(operand);
    }
}

