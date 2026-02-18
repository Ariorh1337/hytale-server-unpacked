/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.file;

import com.hypixel.hytale.procedurallib.file.AssetLoader;
import com.hypixel.hytale.procedurallib.file.AssetPath;
import com.hypixel.hytale.procedurallib.file.FileIO;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nonnull;

public interface FileIOSystem
extends AutoCloseable {
    @Nonnull
    public Path baseRoot();

    @Nonnull
    public PathArray roots();

    @Nonnull
    default public AssetPath resolve(@Nonnull Path path) {
        Path relPath = FileIO.relativize(path, this.baseRoot());
        for (Path root : this.roots().paths) {
            AssetPath assetPath = AssetPath.fromRelative(root, relPath);
            if (!FileIO.exists(assetPath)) continue;
            return assetPath;
        }
        return AssetPath.fromRelative(this.baseRoot(), relPath);
    }

    @Nonnull
    default public <T> T load(@Nonnull AssetPath path, @Nonnull AssetLoader<T> loader) throws IOException {
        if (!Files.exists(path.filepath(), new LinkOption[0])) {
            throw new FileNotFoundException("Unable to find file: " + String.valueOf(path));
        }
        try (InputStream stream = Files.newInputStream(path.filepath(), new OpenOption[0]);){
            T t = loader.load(stream);
            return t;
        }
    }

    @Override
    default public void close() {
    }

    public static final class PathArray {
        final Path[] paths;

        public PathArray(Path ... paths) {
            this.paths = paths;
        }

        public int size() {
            return this.paths.length;
        }

        public Path get(int index) {
            return this.paths[index];
        }
    }

    public static final class Provider {
        private static final DefaultIOFileSystem DEFAULT = new DefaultIOFileSystem();
        private static final ThreadLocal<FileIOSystem> HOLDER = ThreadLocal.withInitial(() -> DEFAULT);

        static FileIOSystem get() {
            return HOLDER.get();
        }

        static void set(@Nonnull FileIOSystem fs) {
            HOLDER.set(fs);
        }

        static void unset() {
            HOLDER.set(DEFAULT);
        }

        static void setRoot(@Nonnull Path path) {
            DEFAULT.setBase(path);
        }

        private static final class DefaultIOFileSystem
        implements FileIOSystem {
            private static final Path DEFAULT_ROOT = Paths.get(".", new String[0]).toAbsolutePath();
            private Path base = DEFAULT_ROOT;
            private PathArray roots = new PathArray(DEFAULT_ROOT);

            private DefaultIOFileSystem() {
            }

            public synchronized void setBase(Path base) {
                this.base = base;
                this.roots = new PathArray(base);
            }

            @Override
            @Nonnull
            public synchronized Path baseRoot() {
                return this.base;
            }

            @Override
            @Nonnull
            public synchronized PathArray roots() {
                return this.roots;
            }
        }
    }
}

