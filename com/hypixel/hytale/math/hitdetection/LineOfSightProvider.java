/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.hitdetection;

public interface LineOfSightProvider {
    public static final LineOfSightProvider DEFAULT_TRUE = (fromX, fromY, fromZ, toX, toY, toZ) -> true;

    public boolean test(double var1, double var3, double var5, double var7, double var9, double var11);
}

