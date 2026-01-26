/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import javax.annotation.Nonnull;

public class TerrainDensity
extends Density {
    @Override
    public double process(@Nonnull Density.Context context) {
        if (context.terrainDensityProvider == null) {
            return 0.0;
        }
        return context.terrainDensityProvider.get(context.position.toVector3i(), context.workerId);
    }
}

