/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenLoadException;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.IWorldGenProvider;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.loader.ChunkGeneratorJsonLoader;
import com.hypixel.hytale.server.worldgen.prefab.PrefabStoreRoot;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class HytaleWorldGenProvider
implements IWorldGenProvider {
    public static final String ID = "Hytale";
    public static final BuilderCodec<HytaleWorldGenProvider> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(HytaleWorldGenProvider.class, HytaleWorldGenProvider::new).documentation("The standard generator for Hytale.")).append(new KeyedCodec<String>("Name", Codec.STRING), (config, s) -> {
        config.name = s;
    }, config -> config.name).documentation("The name of the generator to use. \"*Default*\" if not provided.").add()).append(new KeyedCodec<String>("Path", Codec.STRING), (config, s) -> {
        config.path = s;
    }, config -> config.path).documentation("The path to the world generation configuration. \n\nDefaults to the server provided world generation folder if not set.").add()).build();
    private String name = "Default";
    private String path;

    @Override
    @Nonnull
    public IWorldGen getGenerator() throws WorldGenLoadException {
        Path worldGenPath = this.path != null ? PathUtil.get(this.path) : Universe.getWorldGenPath();
        if (!"Default".equals(this.name) || !Files.exists(worldGenPath.resolve("World.json"), new LinkOption[0])) {
            worldGenPath = worldGenPath.resolve(this.name);
        }
        try {
            return new ChunkGeneratorJsonLoader(new SeedString<SeedStringResource>("ChunkGenerator", new SeedStringResource(PrefabStoreRoot.DEFAULT, worldGenPath)), worldGenPath).load();
        }
        catch (Error e) {
            throw new WorldGenLoadException("Failed to load world gen!", e);
        }
    }

    @Nonnull
    public String toString() {
        return "HytaleWorldGenProvider{name='" + this.name + "', path='" + this.path + "'}";
    }
}

