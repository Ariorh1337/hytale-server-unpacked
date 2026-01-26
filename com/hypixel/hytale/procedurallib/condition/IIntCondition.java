/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.condition;

import com.hypixel.hytale.procedurallib.util.IntToIntFunction;
import javax.annotation.Nonnull;

public interface IIntCondition {
    public boolean eval(int var1);

    default public boolean eval(int seed, @Nonnull IntToIntFunction seedFunction) {
        return this.eval(seedFunction.applyAsInt(seed));
    }
}

