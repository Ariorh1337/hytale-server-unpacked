/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.semver.Semver;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenLoadException;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.IWorldGenProvider;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.WorldGenConfig;
import com.hypixel.hytale.server.worldgen.loader.ChunkGeneratorJsonLoader;
import com.hypixel.hytale.server.worldgen.prefab.PrefabStoreRoot;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HytaleWorldGenProvider
implements IWorldGenProvider {
    public static final String ID = "Hytale";
    public static final String DEFAULT_NAME = "Default";
    public static final Semver MIN_VERSION = new Semver(0L, 0L, 0L);
    public static final WorldGenBuilderCodec CODEC = new WorldGenBuilderCodec((BuilderCodec.BuilderBase<HytaleWorldGenProvider, ?>)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(HytaleWorldGenProvider.class, HytaleWorldGenProvider::new).documentation("The standard generator for Hytale.")).append(new KeyedCodec<String>("Name", Codec.STRING), (config, s) -> {
        config.name = s;
    }, config -> config.name).documentation("The name of the generator to use. \"*Default*\" if not provided.").add()).append(new KeyedCodec<Semver>("Version", Semver.CODEC), (config, v) -> {
        config.version = v;
    }, config -> config.version).documentation("The version of the generator to use. \"0.0.0\" if not provided.").add()).append(new KeyedCodec<String>("Path", Codec.STRING), (config, s) -> {
        config.path = s;
    }, config -> config.path).documentation("The path to the world generation configuration. \n\nDefaults to the server provided world generation folder if not set.").add());
    @Nonnull
    private String name = "Default";
    @Nonnull
    private Semver version = MIN_VERSION;
    @Nullable
    private String path;

    @Nonnull
    public Semver getVersion() {
        return this.version;
    }

    @Override
    @Nonnull
    public IWorldGen getGenerator() throws WorldGenLoadException {
        Path worldGenPath;
        if (this.path != null) {
            worldGenPath = Path.of(this.path, new String[0]);
            if (!PathUtil.isInTrustedRoot(worldGenPath)) {
                throw new WorldGenLoadException("World gen path must be within a trusted directory: " + this.path);
            }
        } else {
            worldGenPath = Universe.getWorldGenPath();
        }
        if (!DEFAULT_NAME.equals(this.name) || !Files.exists(worldGenPath.resolve("World.json"), new LinkOption[0])) {
            Path resolved = PathUtil.resolvePathWithinDir(worldGenPath, this.name);
            if (resolved == null) {
                throw new WorldGenLoadException("Invalid world gen name: " + this.name);
            }
            worldGenPath = resolved;
        }
        try {
            WorldGenConfig config = new WorldGenConfig(worldGenPath, this.name, this.version);
            return new ChunkGeneratorJsonLoader(new SeedString<SeedStringResource>("ChunkGenerator", new SeedStringResource(PrefabStoreRoot.DEFAULT, config)), config).load();
        }
        catch (Error e) {
            throw new WorldGenLoadException("Failed to load world gen!", e);
        }
    }

    public String toString() {
        return "HytaleWorldGenProvider{name='" + this.name + "', version=" + String.valueOf(this.version) + ", path='" + this.path + "'}";
    }

    public static class WorldGenBuilderCodec
    extends BuilderCodec<HytaleWorldGenProvider> {
        private final Object lock = new Object();
        private final Map<String, Semver> versions = new Object2ObjectOpenHashMap<String, Semver>();

        protected WorldGenBuilderCodec(@Nonnull BuilderCodec.BuilderBase<HytaleWorldGenProvider, ?> builder) {
            super(builder);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public HytaleWorldGenProvider getDefaultValue(ExtraInfo extraInfo) {
            HytaleWorldGenProvider value = new HytaleWorldGenProvider();
            Object object = this.lock;
            synchronized (object) {
                value.version = this.versions.getOrDefault(HytaleWorldGenProvider.DEFAULT_NAME, MIN_VERSION);
            }
            this.afterDecode(value, extraInfo);
            return value;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void setVersions(@Nonnull Map<String, Semver> versions) {
            Object object = this.lock;
            synchronized (object) {
                this.versions.clear();
                this.versions.putAll(versions);
            }
        }
    }
}

