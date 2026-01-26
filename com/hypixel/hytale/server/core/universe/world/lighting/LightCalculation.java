/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.lighting;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.lighting.CalculationResult;
import javax.annotation.Nonnull;

public interface LightCalculation {
    public void init(@Nonnull WorldChunk var1);

    @Nonnull
    public CalculationResult calculateLight(@Nonnull Vector3i var1);

    public boolean invalidateLightAtBlock(@Nonnull WorldChunk var1, int var2, int var3, int var4, @Nonnull BlockType var5, int var6, int var7);

    public boolean invalidateLightInChunkSections(@Nonnull WorldChunk var1, int var2, int var3);
}

