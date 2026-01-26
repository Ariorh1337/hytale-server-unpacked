/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage.resources;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.IResourceStorage;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldProvider;
import com.hypixel.hytale.server.core.universe.world.storage.resources.IResourceStorageProvider;
import com.hypixel.hytale.server.core.util.BsonUtil;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import org.bson.BsonDocument;

public class DiskResourceStorageProvider
implements IResourceStorageProvider {
    public static final String ID = "Disk";
    public static final BuilderCodec<DiskResourceStorageProvider> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(DiskResourceStorageProvider.class, DiskResourceStorageProvider::new).append(new KeyedCodec<String>("Path", Codec.STRING), (o, s) -> {
        o.path = s;
    }, o -> o.path).add()).build();
    @Nonnull
    private String path = "resources";

    @Nonnull
    public String getPath() {
        return this.path;
    }

    @Override
    @Nonnull
    public <T extends WorldProvider> IResourceStorage getResourceStorage(@Nonnull World world) {
        return new DiskResourceStorage(world.getSavePath().resolve(this.path));
    }

    @Nonnull
    public String toString() {
        return "DiskResourceStorageProvider{path=" + this.path + "}";
    }

    @Deprecated(forRemoval=true)
    public static void migrateFiles(@Nonnull World world) {
        Path entityStorePath;
        Path resourcesPath = world.getSavePath().resolve("resources");
        Path chunkStorePath = resourcesPath.resolve("chunkstore");
        if (Files.exists(chunkStorePath, new LinkOption[0])) {
            try {
                FileUtil.moveDirectoryContents(chunkStorePath, resourcesPath, StandardCopyOption.REPLACE_EXISTING);
                FileUtil.deleteDirectory(chunkStorePath);
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to migrate old chunkstore resources!", e);
            }
        }
        if (Files.exists(entityStorePath = resourcesPath.resolve("entitystore"), new LinkOption[0])) {
            try {
                FileUtil.moveDirectoryContents(entityStorePath, resourcesPath, StandardCopyOption.REPLACE_EXISTING);
                FileUtil.deleteDirectory(entityStorePath);
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to migrate old entitystore resources!", e);
            }
        }
    }

    public static class DiskResourceStorage
    implements IResourceStorage {
        private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
        public static final String FILE_EXTENSION = ".json";
        @Nonnull
        private final Path path;

        public DiskResourceStorage(@Nonnull Path path) {
            this.path = path;
            if (!Options.getOptionSet().has(Options.BARE)) {
                try {
                    Files.createDirectories(path, new FileAttribute[0]);
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to create Resources directory", e);
                }
            }
        }

        @Override
        @Nonnull
        public <T extends Resource<ECS_TYPE>, ECS_TYPE> CompletableFuture<T> load(@Nonnull Store<ECS_TYPE> store, @Nonnull ComponentRegistry.Data<ECS_TYPE> data, @Nonnull ResourceType<ECS_TYPE, T> resourceType) {
            BuilderCodec codec = data.getResourceCodec(resourceType);
            if (codec == null) {
                return CompletableFuture.completedFuture(data.createResource(resourceType));
            }
            return CompletableFuture.supplyAsync(SneakyThrow.sneakySupplier(() -> {
                BasicFileAttributes attributes;
                String id = data.getResourceId(resourceType);
                Path file = this.path.resolve(id + FILE_EXTENSION);
                try {
                    attributes = Files.readAttributes(file, BasicFileAttributes.class, new LinkOption[0]);
                }
                catch (IOException ignored) {
                    LOGGER.at(Level.FINE).log("File '%s' was not found, using the default file", file);
                    return data.createResource(resourceType);
                }
                if (attributes.size() == 0L) {
                    LOGGER.at(Level.WARNING).log("Error loading file %s, file was found to be entirely empty, using the default file", file);
                    return data.createResource(resourceType);
                }
                try {
                    Resource resource = (Resource)RawJsonReader.readSync(file, codec, LOGGER);
                    return resource != null ? resource : data.createResource(resourceType);
                }
                catch (IOException e) {
                    ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to load resource from %s, using default", file);
                    return data.createResource(resourceType);
                }
            }));
        }

        @Override
        @Nonnull
        public <T extends Resource<ECS_TYPE>, ECS_TYPE> CompletableFuture<Void> save(@Nonnull Store<ECS_TYPE> store, @Nonnull ComponentRegistry.Data<ECS_TYPE> data, @Nonnull ResourceType<ECS_TYPE, T> resourceType, T resource) {
            BuilderCodec<T> codec = data.getResourceCodec(resourceType);
            if (codec == null) {
                return CompletableFuture.completedFuture(null);
            }
            String id = data.getResourceId(resourceType);
            Path file = this.path.resolve(id + FILE_EXTENSION);
            ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
            BsonDocument document = codec.encode((Object)resource, extraInfo).asDocument();
            extraInfo.getValidationResults().logOrThrowValidatorExceptions(LOGGER);
            return BsonUtil.writeDocument(file, document);
        }

        @Override
        @Nonnull
        public <T extends Resource<ECS_TYPE>, ECS_TYPE> CompletableFuture<Void> remove(@Nonnull Store<ECS_TYPE> store, @Nonnull ComponentRegistry.Data<ECS_TYPE> data, @Nonnull ResourceType<ECS_TYPE, T> resourceType) {
            String id = data.getResourceId(resourceType);
            if (id == null) {
                return CompletableFuture.completedFuture(null);
            }
            Path file = this.path.resolve(id + FILE_EXTENSION);
            try {
                Files.deleteIfExists(file);
                return CompletableFuture.completedFuture(null);
            }
            catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }
        }
    }
}

