/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.file;

import com.hypixel.hytale.procedurallib.file.AssetLoader;
import com.hypixel.hytale.procedurallib.file.AssetPath;
import com.hypixel.hytale.procedurallib.file.FileIOSystem;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface FileIO {
    public static final Hash.Strategy<Path> PATH_STRATEGY = new Hash.Strategy<Path>(){

        @Override
        public int hashCode(Path o) {
            return FileIO.hashCode(o);
        }

        @Override
        public boolean equals(Path a, Path b) {
            return FileIO.equals(a, b);
        }
    };

    public static void setDefaultRoot(@Nonnull Path path) {
        FileIOSystem.Provider.setRoot(path);
    }

    @Nonnull
    public static <FS extends FileIOSystem> FS openFileIOSystem(@Nonnull FS fs) {
        FileIOSystem.Provider.set(fs);
        return fs;
    }

    public static void closeFileIOSystem(@Nonnull FileIOSystem fs) {
        FileIOSystem.Provider.unset();
    }

    public static boolean exists(@Nonnull AssetPath path) {
        return Files.exists(path.filepath(), new LinkOption[0]);
    }

    public static boolean exists(@Nonnull Path root, @Nonnull Path path) {
        return Files.exists(FileIO.append(root, path), new LinkOption[0]);
    }

    @Nonnull
    public static AssetPath resolve(@Nonnull Path path) {
        FileIOSystem fs = FileIOSystem.Provider.get();
        return fs.resolve(path);
    }

    @Nonnull
    public static <T> T load(@Nonnull AssetPath assetPath, @Nonnull AssetLoader<T> loader) throws IOException {
        FileIOSystem fs = FileIOSystem.Provider.get();
        return fs.load(assetPath, loader);
    }

    @Nonnull
    public static <T> T load(@Nonnull Path path, @Nonnull AssetLoader<T> loader) throws IOException {
        FileIOSystem fs = FileIOSystem.Provider.get();
        AssetPath assetPath = fs.resolve(path);
        return fs.load(assetPath, loader);
    }

    @Nonnull
    public static List<AssetPath> list(@Nonnull Path path, @Nonnull Predicate<AssetPath> matcher, @Nonnull UnaryOperator<AssetPath> disableOp) throws IOException {
        FileIOSystem fs = FileIOSystem.Provider.get();
        Path assetDirPath = FileIO.relativize(path, fs.baseRoot());
        ObjectArrayList<AssetPath> paths = new ObjectArrayList<AssetPath>();
        ObjectOpenHashSet visited = new ObjectOpenHashSet();
        ObjectOpenHashSet<AssetPath> disabled = new ObjectOpenHashSet<AssetPath>();
        for (Path root : fs.roots().paths) {
            Path rootAssetDirPath = FileIO.append(root, assetDirPath);
            if (!Files.exists(rootAssetDirPath, new LinkOption[0]) || !Files.isDirectory(rootAssetDirPath, new LinkOption[0])) continue;
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(rootAssetDirPath);){
                visited.addAll(disabled);
                disabled.clear();
                for (Path filepath : dirStream) {
                    AssetPath assetPath = AssetPath.fromAbsolute(root, filepath);
                    AssetPath disabledPath = (AssetPath)disableOp.apply(assetPath);
                    if (disabledPath != assetPath) {
                        disabled.add(disabledPath);
                        continue;
                    }
                    if (!matcher.test(assetPath) || !visited.add(assetPath)) continue;
                    paths.add(assetPath);
                }
            }
        }
        return paths;
    }

    public static boolean startsWith(Path path, Path prefix) {
        if (prefix.getNameCount() > path.getNameCount()) {
            return false;
        }
        boolean match = true;
        for (int i = 0; match && i < prefix.getNameCount(); ++i) {
            match = path.getName(i).toString().equals(prefix.getName(i).toString());
        }
        return match;
    }

    public static Path relativize(Path child, Path parent) {
        if (child.getNameCount() < parent.getNameCount()) {
            return child;
        }
        if (!FileIO.startsWith(child, parent)) {
            return child;
        }
        return child.subpath(parent.getNameCount(), child.getNameCount());
    }

    public static Path append(Path root, Path path) {
        if (path.getFileSystem() == root.getFileSystem()) {
            return root.resolve(path);
        }
        Path out = root;
        for (int i = 0; i < path.getNameCount(); ++i) {
            out = out.resolve(path.getName(i).toString());
        }
        return out;
    }

    public static boolean equals(@Nullable Path a, @Nullable Path b) {
        return a == b || a != null && b != null && a.getNameCount() == b.getNameCount() && FileIO.startsWith(a, b);
    }

    public static int hashCode(@Nullable Path path) {
        if (path == null) {
            return 0;
        }
        int hashcode = 1;
        for (int i = 0; i < path.getNameCount(); ++i) {
            hashcode = hashcode * 31 + path.getName(i).toString().hashCode();
        }
        return hashcode;
    }
}

