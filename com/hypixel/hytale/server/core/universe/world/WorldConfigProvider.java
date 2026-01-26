/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world;

import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public interface WorldConfigProvider {
    @Nonnull
    default public CompletableFuture<WorldConfig> load(@Nonnull Path savePath, String name) {
        Path oldPath = savePath.resolve("config.bson");
        Path path = savePath.resolve("config.json");
        if (Files.exists(oldPath, new LinkOption[0]) && !Files.exists(path, new LinkOption[0])) {
            try {
                Files.move(oldPath, path, new CopyOption[0]);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return WorldConfig.load(path);
    }

    @Nonnull
    default public CompletableFuture<Void> save(@Nonnull Path savePath, WorldConfig config, World world) {
        return WorldConfig.save(savePath.resolve("config.json"), config);
    }

    public static class Default
    implements WorldConfigProvider {
    }
}

