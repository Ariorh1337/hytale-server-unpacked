/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.file;

import com.hypixel.hytale.procedurallib.file.FileIO;
import java.nio.file.Path;
import java.util.Comparator;
import javax.annotation.Nonnull;

public final class AssetPath {
    private final Path path;
    private final Path filepath;
    private final transient int hash;
    public static final Comparator<AssetPath> COMPARATOR = (a, b) -> {
        int max = Math.min(a.filepath.getNameCount(), b.filepath.getNameCount());
        for (int i = 0; i < max; ++i) {
            int comp = a.filepath.getName(i).toString().compareTo(b.filepath.getName(i).toString());
            if (comp == 0) continue;
            return comp;
        }
        return Integer.compare(a.filepath.getNameCount(), b.filepath.getNameCount());
    };

    private AssetPath(@Nonnull Path path, @Nonnull Path filepath) {
        this.path = path;
        this.filepath = filepath;
        this.hash = FileIO.hashCode(path);
    }

    @Nonnull
    public AssetPath rename(@Nonnull String filename) {
        Path rel = this.path.getParent().resolve(filename);
        Path path = this.filepath.getParent().resolve(filename);
        return new AssetPath(rel, path);
    }

    @Nonnull
    public Path path() {
        return this.path;
    }

    @Nonnull
    public Path filepath() {
        return this.filepath;
    }

    @Nonnull
    public String getFileName() {
        return this.filepath.getFileName().toString();
    }

    public String toString() {
        return "AssetPath{path=" + String.valueOf(this.path) + ", filepath=" + String.valueOf(this.filepath) + "}";
    }

    public int hashCode() {
        return this.hash;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AssetPath)) return false;
        AssetPath other = (AssetPath)obj;
        if (this.hash != other.hash) return false;
        if (!FileIO.equals(this.path, other.path)) return false;
        return true;
    }

    public static AssetPath fromAbsolute(@Nonnull Path root, @Nonnull Path filepath) {
        assert (root.getNameCount() == 0 || FileIO.startsWith(filepath, root));
        Path relPath = FileIO.relativize(filepath, root);
        return new AssetPath(relPath, filepath);
    }

    public static AssetPath fromRelative(@Nonnull Path root, @Nonnull Path assetPath) {
        assert (root.getNameCount() == 0 || !FileIO.startsWith(assetPath, root));
        Path filepath = FileIO.append(root, assetPath);
        return new AssetPath(assetPath, filepath);
    }
}

