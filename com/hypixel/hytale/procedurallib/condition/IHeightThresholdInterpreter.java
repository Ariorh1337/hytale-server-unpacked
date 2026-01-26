/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.condition;

public interface IHeightThresholdInterpreter {
    public int getLowestNonOne();

    public int getHighestNonZero();

    public float getThreshold(int var1, double var2, double var4, int var6);

    public float getThreshold(int var1, double var2, double var4, int var6, double var7);

    public double getContext(int var1, double var2, double var4);

    public int getLength();

    default public boolean isSpawnable(int height) {
        return height >= this.getLowestNonOne() && height <= this.getHighestNonZero();
    }

    public static float lerp(float from, float to, float t) {
        return from + (to - from) * t;
    }
}

