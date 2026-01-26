/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.condition;

import java.util.function.IntToDoubleFunction;
import javax.annotation.Nonnull;

public interface IDoubleCondition {
    public boolean eval(double var1);

    default public boolean eval(int seed, @Nonnull IntToDoubleFunction seedFunction) {
        return this.eval(seedFunction.applyAsDouble(seed));
    }
}

