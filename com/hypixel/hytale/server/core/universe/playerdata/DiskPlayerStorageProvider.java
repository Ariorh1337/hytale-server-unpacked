/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.playerdata;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.universe.playerdata.PlayerStorage;
import com.hypixel.hytale.server.core.universe.playerdata.PlayerStorageProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.BsonUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.bson.BsonDocument;

public class DiskPlayerStorageProvider
implements PlayerStorageProvider {
    public static final String ID = "Disk";
    public static final BuilderCodec<DiskPlayerStorageProvider> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(DiskPlayerStorageProvider.class, DiskPlayerStorageProvider::new).append(new KeyedCodec<String>("Path", Codec.STRING), (o, s) -> {
        o.path = PathUtil.get(s);
    }, o -> o.path.toString()).add()).build();
    @Nonnull
    private Path path = Constants.UNIVERSE_PATH.resolve("players");

    @Nonnull
    public Path getPath() {
        return this.path;
    }

    @Override
    @Nonnull
    public PlayerStorage getPlayerStorage() {
        return new DiskPlayerStorage(this.path);
    }

    @Nonnull
    public String toString() {
        return "DiskPlayerStorageProvider{path=" + String.valueOf(this.path) + "}";
    }

    public static class DiskPlayerStorage
    implements PlayerStorage {
        public static final String FILE_EXTENSION = ".json";
        @Nonnull
        private final Path path;

        public DiskPlayerStorage(@Nonnull Path path) {
            this.path = path;
            if (!Options.getOptionSet().has(Options.BARE)) {
                try {
                    Files.createDirectories(path, new FileAttribute[0]);
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to create players directory", e);
                }
            }
        }

        @Override
        @Nonnull
        public CompletableFuture<Holder<EntityStore>> load(@Nonnull UUID uuid) {
            Path file = this.path.resolve(String.valueOf(uuid) + FILE_EXTENSION);
            return BsonUtil.readDocument(file).thenApply(bsonDocument -> {
                if (bsonDocument == null) {
                    bsonDocument = new BsonDocument();
                }
                return EntityStore.REGISTRY.deserialize((BsonDocument)bsonDocument);
            });
        }

        @Override
        @Nonnull
        public CompletableFuture<Void> save(@Nonnull UUID uuid, @Nonnull Holder<EntityStore> holder) {
            Path file = this.path.resolve(String.valueOf(uuid) + FILE_EXTENSION);
            BsonDocument document = EntityStore.REGISTRY.serialize(holder);
            return BsonUtil.writeDocument(file, document);
        }

        @Override
        @Nonnull
        public CompletableFuture<Void> remove(@Nonnull UUID uuid) {
            Path file = this.path.resolve(String.valueOf(uuid) + FILE_EXTENSION);
            try {
                Files.deleteIfExists(file);
                return CompletableFuture.completedFuture(null);
            }
            catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }
        }

        @Override
        @Nonnull
        public Set<UUID> getPlayers() throws IOException {
            try (Stream<Path> stream = Files.list(this.path);){
                Set<UUID> set = stream.map(p -> {
                    String fileName = p.getFileName().toString();
                    if (!fileName.endsWith(FILE_EXTENSION)) {
                        return null;
                    }
                    try {
                        return UUID.fromString(fileName.substring(0, fileName.length() - FILE_EXTENSION.length()));
                    }
                    catch (IllegalArgumentException e) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toSet());
                return set;
            }
        }
    }
}

