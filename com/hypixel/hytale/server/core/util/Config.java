/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.util.BsonUtil;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class Config<T> {
    @Nonnull
    private final Path path;
    private final String name;
    private final BuilderCodec<T> codec;
    @Nullable
    private T config;
    @Nullable
    private CompletableFuture<T> loadingConfig;

    public Config(@Nonnull Path path, String name, BuilderCodec<T> codec) {
        this.path = path.resolve(name + ".json");
        this.name = name;
        this.codec = codec;
    }

    @Nonnull
    @Deprecated(forRemoval=true)
    public static <T> Config<T> preloadedConfig(@Nonnull Path path, String name, BuilderCodec<T> codec, T config) {
        Config<T> c = new Config<T>(path, name, codec);
        c.config = config;
        return c;
    }

    @Nonnull
    public CompletableFuture<T> load() {
        if (this.loadingConfig != null) {
            return this.loadingConfig;
        }
        if (!Files.exists(this.path, new LinkOption[0])) {
            this.config = this.codec.getDefaultValue();
            return CompletableFuture.completedFuture(this.config);
        }
        this.loadingConfig = CompletableFuture.supplyAsync(SneakyThrow.sneakySupplier(() -> {
            this.config = RawJsonReader.readSync(this.path, this.codec, HytaleLogger.getLogger());
            this.loadingConfig = null;
            return this.config;
        }));
        return this.loadingConfig;
    }

    public T get() {
        if (this.config == null && this.loadingConfig == null) {
            throw new IllegalStateException("Config is not loaded");
        }
        if (this.loadingConfig != null) {
            return this.loadingConfig.join();
        }
        return this.config;
    }

    @Nonnull
    public CompletableFuture<Void> save() {
        if (this.config == null && this.loadingConfig == null) {
            throw new IllegalStateException("Config is not loaded");
        }
        if (this.loadingConfig != null) {
            return CompletableFuture.completedFuture(null);
        }
        return BsonUtil.writeDocument(this.path, (BsonDocument)this.codec.encode((Object)this.config, new ExtraInfo()));
    }
}

