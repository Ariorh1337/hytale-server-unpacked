/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.condition;

import com.hypixel.hytale.procedurallib.condition.ICoordinateRndCondition;
import com.hypixel.hytale.procedurallib.condition.IHeightThresholdInterpreter;
import java.util.Random;
import javax.annotation.Nonnull;

public class HeightCondition
implements ICoordinateRndCondition {
    protected final IHeightThresholdInterpreter interpreter;

    public HeightCondition(IHeightThresholdInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public boolean eval(int seed, int x, int z, int y, @Nonnull Random random) {
        double threshold = this.interpreter.getThreshold(seed, x, z, y);
        return threshold > 0.0 && (threshold >= 1.0 || threshold > random.nextDouble());
    }

    @Nonnull
    public String toString() {
        return "HeightCondition{interpreter=" + String.valueOf(this.interpreter) + "}";
    }
}

