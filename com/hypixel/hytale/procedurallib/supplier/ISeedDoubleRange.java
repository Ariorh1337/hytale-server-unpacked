/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.supplier;

@FunctionalInterface
public interface ISeedDoubleRange {
    public static final ISeedDoubleRange DIRECT = (seed, value) -> value;

    public double getValue(int var1, double var2);
}

