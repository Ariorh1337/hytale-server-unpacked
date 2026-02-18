/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.builtin.worldgen.WorldGenPlugin;
import com.hypixel.hytale.procedurallib.file.AssetLoader;
import com.hypixel.hytale.procedurallib.file.AssetPath;
import com.hypixel.hytale.procedurallib.file.FileIO;
import com.hypixel.hytale.procedurallib.file.FileIOSystem;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.worldgen.WorldGenConfig;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class AssetFileSystem
implements FileIOSystem {
    private static final Hash.Strategy<Path> PATH_STRATEGY = new Hash.Strategy<Path>(){

        @Override
        public int hashCode(Path o) {
            return FileIO.hashCode(o);
        }

        @Override
        public boolean equals(Path a, Path b) {
            return FileIO.equals(a, b);
        }
    };
    private final Path root;
    private final FileIOSystem.PathArray packRoots;
    private final List<AssetPack> packs;
    private final Object2ObjectMap<Path, AssetPath> files = new Object2ObjectOpenCustomHashMap<Path, AssetPath>(PATH_STRATEGY);
    private final Object2ObjectMap<AssetPath, Resource<?>> resources = new Object2ObjectOpenHashMap();

    public AssetFileSystem(@Nonnull WorldGenConfig config) {
        Path root = AssetModule.get().getBaseAssetPack().getRoot();
        Path assetPath = FileIO.relativize(config.path(), root);
        this.root = root;
        this.packs = AssetFileSystem.getAssetPacks(config, packRoot -> FileIO.exists(packRoot, assetPath));
        this.packRoots = new FileIOSystem.PathArray(AssetFileSystem.getAssetRoots(this.packs));
    }

    @Override
    @Nonnull
    public Path baseRoot() {
        return this.root;
    }

    @Override
    @Nonnull
    public FileIOSystem.PathArray roots() {
        return this.packRoots;
    }

    @Override
    @Nonnull
    public AssetPath resolve(@Nonnull Path path) {
        Path relPath = FileIO.relativize(path, this.root);
        AssetPath assetPath = (AssetPath)this.files.get(relPath);
        if (assetPath == null) {
            assetPath = FileIOSystem.super.resolve(path);
            this.files.put(relPath, assetPath);
        }
        return assetPath;
    }

    @Override
    @Nonnull
    public <T> T load(@Nonnull AssetPath path, @Nonnull AssetLoader<T> loader) throws IOException {
        Resource<T> resource = (Resource<T>)this.resources.get(path);
        if (resource == null) {
            T value = FileIOSystem.super.load(path, loader);
            resource = new Resource<T>(value, loader.type());
            this.resources.put(path, resource);
        } else if (resource.type() != loader.type()) {
            throw new IllegalStateException("Resource type mismatch: expected " + String.valueOf(loader.type()) + " but found " + String.valueOf(resource.type));
        }
        return loader.type().cast(resource.value);
    }

    @Override
    public void close() {
        this.files.clear();
        this.resources.clear();
        FileIO.closeFileIOSystem(this);
    }

    public List<AssetPack> packs() {
        return this.packs;
    }

    public static List<AssetPack> getAssetPacks(@Nonnull WorldGenConfig config, @Nonnull Predicate<Path> filter) {
        AssetPack pack;
        int i;
        AssetModule assets = AssetModule.get();
        Path versionsDir = WorldGenPlugin.getVersionsPath();
        List<AssetPack> allPacks = assets.getAssetPacks();
        ObjectArrayList<AssetPack> packs = new ObjectArrayList<AssetPack>(allPacks.size());
        for (i = allPacks.size() - 1; i >= 1; --i) {
            pack = allPacks.get(i);
            if (FileIO.startsWith(pack.getRoot(), versionsDir) || !filter.test(pack.getRoot())) continue;
            packs.add(allPacks.get(i));
        }
        for (i = allPacks.size() - 1; i >= 1; --i) {
            pack = allPacks.get(i);
            if (!FileIO.startsWith(pack.getRoot(), versionsDir) || pack.getManifest().getVersion().compareTo(config.version()) > 0 || !filter.test(pack.getRoot())) continue;
            packs.add(allPacks.get(i));
        }
        packs.add(allPacks.getFirst());
        return List.copyOf(packs);
    }

    public static Path[] getAssetRoots(@Nonnull List<AssetPack> packs) {
        Path[] roots = new Path[packs.size()];
        for (int i = 0; i < packs.size(); ++i) {
            roots[i] = packs.get(i).getRoot();
        }
        return roots;
    }

    public record Resource<T>(@Nonnull T value, @Nonnull Class<T> type) {
    }
}

