/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.condition;

import com.hypixel.hytale.procedurallib.condition.IDoubleCondition;
import com.hypixel.hytale.procedurallib.condition.IDoubleThreshold;
import javax.annotation.Nonnull;

public class DoubleThresholdCondition
implements IDoubleCondition {
    protected final IDoubleThreshold threshold;

    public DoubleThresholdCondition(IDoubleThreshold threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean eval(double value) {
        return this.threshold.eval(value);
    }

    @Nonnull
    public String toString() {
        return "DoubleThresholdCondition{threshold=" + String.valueOf(this.threshold) + "}";
    }
}

