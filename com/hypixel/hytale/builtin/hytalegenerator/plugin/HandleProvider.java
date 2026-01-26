/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.plugin;

import com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator.ChunkRequest;
import com.hypixel.hytale.builtin.hytalegenerator.plugin.Handle;
import com.hypixel.hytale.builtin.hytalegenerator.plugin.HytaleGenerator;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenLoadException;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.IWorldGenProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HandleProvider
implements IWorldGenProvider {
    public static final String ID = "HytaleGenerator";
    public static final String DEFAULT_WORLD_STRUCTURE_NAME = "Default";
    @Nonnull
    private final HytaleGenerator plugin;
    @Nonnull
    private String worldStructureName = "Default";

    public HandleProvider(@Nonnull HytaleGenerator plugin) {
        this.plugin = plugin;
    }

    public void setWorldStructureName(@Nullable String worldStructureName) {
        this.worldStructureName = worldStructureName;
    }

    @Nonnull
    public String getWorldStructureName() {
        return this.worldStructureName;
    }

    @Override
    public IWorldGen getGenerator() throws WorldGenLoadException {
        return new Handle(this.plugin, new ChunkRequest.GeneratorProfile(this.worldStructureName, 0));
    }
}

