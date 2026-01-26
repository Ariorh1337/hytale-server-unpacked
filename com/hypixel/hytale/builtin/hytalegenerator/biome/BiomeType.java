/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.biome;

import com.hypixel.hytale.builtin.hytalegenerator.biome.EnvironmentSource;
import com.hypixel.hytale.builtin.hytalegenerator.biome.MaterialSource;
import com.hypixel.hytale.builtin.hytalegenerator.biome.PropsSource;
import com.hypixel.hytale.builtin.hytalegenerator.biome.TintSource;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import javax.annotation.Nonnull;

public interface BiomeType
extends MaterialSource,
PropsSource,
EnvironmentSource,
TintSource {
    public String getBiomeName();

    @Nonnull
    public Density getTerrainDensity();
}

