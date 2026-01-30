package com.hypixel.hytale.server.worldgen.loader;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.builtin.worldgen.FeatureFlags;
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

public class AssetFileSystem implements FileIOSystem {
   private static final Hash.Strategy<Path> PATH_STRATEGY = new Hash.Strategy<Path>() {
      public int hashCode(Path o) {
         return FileIO.hashCode(o);
      }

      public boolean equals(Path a, Path b) {
         return FileIO.equals(a, b);
      }
   };
   private final Path root;
   private final FileIOSystem.PathArray packRoots;
   private final Object2ObjectMap<Path, AssetPath> files = new Object2ObjectOpenCustomHashMap<>(PATH_STRATEGY);
   private final Object2ObjectMap<AssetPath, AssetFileSystem.Resource<?>> resources = new Object2ObjectOpenHashMap<>();

   public AssetFileSystem(@Nonnull WorldGenConfig config) {
      Path root = AssetModule.get().getBaseAssetPack().getRoot();
      Path assetPath = FileIO.relativize(config.path(), root);
      this.root = root;
      this.packRoots = new FileIOSystem.PathArray(getAssetRoots(config, packRoot -> FileIO.exists(packRoot, assetPath)));
   }

   @Nonnull
   @Override
   public Path baseRoot() {
      return this.root;
   }

   @Nonnull
   @Override
   public FileIOSystem.PathArray roots() {
      return this.packRoots;
   }

   @Nonnull
   @Override
   public AssetPath resolve(@Nonnull Path path) {
      Path relPath = FileIO.relativize(path, this.root);
      AssetPath assetPath = this.files.get(relPath);
      if (assetPath == null) {
         assetPath = FileIOSystem.super.resolve(path);
         this.files.put(relPath, assetPath);
      }

      return assetPath;
   }

   @Nonnull
   @Override
   public <T> T load(@Nonnull AssetPath path, @Nonnull AssetLoader<T> loader) throws IOException {
      AssetFileSystem.Resource<?> resource = this.resources.get(path);
      if (resource == null) {
         T value = FileIOSystem.super.load(path, loader);
         resource = new AssetFileSystem.Resource<>(value, loader.type());
         this.resources.put(path, resource);
      } else if (resource.type() != loader.type()) {
         throw new IllegalStateException("Resource type mismatch: expected " + loader.type() + " but found " + resource.type);
      }

      return loader.type().cast(resource.value);
   }

   @Override
   public void close() {
      this.files.clear();
      this.resources.clear();
      FileIO.closeFileIOSystem(this);
   }

   public static Path[] getAssetRoots(@Nonnull WorldGenConfig config, @Nonnull Predicate<Path> filter) {
      AssetModule assets = AssetModule.get();
      List<AssetPack> packs = assets.getAssetPacks();
      ObjectArrayList<Path> roots = new ObjectArrayList<>(packs.size());
      Path versionsDir = WorldGenPlugin.getVersionsPath();

      for (int i = packs.size() - 1; i >= 1; i--) {
         AssetPack pack = packs.get(i);
         if (!FileIO.startsWith(pack.getRoot(), versionsDir) && filter.test(pack.getRoot())) {
            roots.add(packs.get(i).getRoot());
         }
      }

      if (FeatureFlags.VERSION_OVERRIDES) {
         for (int i = packs.size() - 1; i >= 1; i--) {
            AssetPack pack = packs.get(i);
            if (FileIO.startsWith(pack.getRoot(), versionsDir)
               && pack.getManifest().getVersion().compareTo(config.version()) <= 0
               && filter.test(pack.getRoot())) {
               roots.add(packs.get(i).getRoot());
            }
         }
      }

      roots.add(packs.getFirst().getRoot());
      return roots.toArray(Path[]::new);
   }

   public record Resource<T>(@Nonnull T value, @Nonnull Class<T> type) {
   }
}
