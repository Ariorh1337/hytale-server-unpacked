/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.datastore;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.datastore.DataStore;
import com.hypixel.hytale.server.core.util.BsonUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonValue;

public class DiskDataStore<T>
implements DataStore<T> {
    private static final String EXTENSION = ".json";
    private static final int EXTENSION_LEN = ".json".length();
    private static final String EXTENSION_BACKUP = ".json.bak";
    private static final String GLOB = "*.json";
    private static final String GLOB_WITH_BACKUP = "*.{json,json.bak}";
    @Nonnull
    private final HytaleLogger logger;
    @Nonnull
    private final Path path;
    private final BuilderCodec<T> codec;

    public DiskDataStore(@Nonnull String path, BuilderCodec<T> codec) {
        this.logger = HytaleLogger.get("DataStore|" + path);
        this.path = Universe.get().getPath().resolve(path);
        this.codec = codec;
        if (Files.isDirectory(this.path, new LinkOption[0])) {
            try (DirectoryStream<Path> paths = Files.newDirectoryStream(this.path, "*.bson");){
                for (Path oldPath : paths) {
                    Path newPath = DiskDataStore.getPathFromId(this.path, DiskDataStore.getIdFromPath(oldPath));
                    try {
                        Files.move(oldPath, newPath, new CopyOption[0]);
                    }
                    catch (IOException iOException) {}
                }
            }
            catch (IOException e) {
                ((HytaleLogger.Api)this.logger.at(Level.SEVERE).withCause(e)).log("Failed to migrate files form .bson to .json!");
            }
        }
    }

    @Nonnull
    public Path getPath() {
        return this.path;
    }

    @Override
    public BuilderCodec<T> getCodec() {
        return this.codec;
    }

    @Override
    @Nullable
    public T load(String id) throws IOException {
        Path filePath = DiskDataStore.getPathFromId(this.path, id);
        return Files.exists(filePath, new LinkOption[0]) ? (T)this.load0(filePath) : null;
    }

    @Override
    public void save(String id, T value) {
        ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
        BsonValue bsonValue = this.codec.encode((Object)value, extraInfo);
        extraInfo.getValidationResults().logOrThrowValidatorExceptions(this.logger);
        BsonUtil.writeDocument(DiskDataStore.getPathFromId(this.path, id), bsonValue.asDocument()).join();
    }

    @Override
    public void remove(String id) throws IOException {
        Files.deleteIfExists(DiskDataStore.getPathFromId(this.path, id));
        Files.deleteIfExists(DiskDataStore.getBackupPathFromId(this.path, id));
    }

    @Override
    @Nonnull
    public List<String> list() throws IOException {
        ObjectArrayList<String> list = new ObjectArrayList<String>();
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(this.path, GLOB);){
            for (Path path : paths) {
                list.add(DiskDataStore.getIdFromPath(path));
            }
        }
        return list;
    }

    @Override
    @Nonnull
    public Map<String, T> loadAll() throws IOException {
        Object2ObjectOpenHashMap<String, T> map = new Object2ObjectOpenHashMap<String, T>();
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(this.path, GLOB);){
            for (Path path : paths) {
                map.put(DiskDataStore.getIdFromPath(path), this.load0(path));
            }
        }
        return map;
    }

    @Override
    public void removeAll() throws IOException {
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(this.path, GLOB_WITH_BACKUP);){
            for (Path path : paths) {
                Files.delete(path);
            }
        }
    }

    @Nullable
    protected T load0(@Nonnull Path path) throws IOException {
        return RawJsonReader.readSync(path, this.codec, this.logger);
    }

    @Nonnull
    protected static Path getPathFromId(@Nonnull Path path, String id) {
        return path.resolve(id + EXTENSION);
    }

    @Nonnull
    protected static Path getBackupPathFromId(@Nonnull Path path, String id) {
        return path.resolve(id + EXTENSION_BACKUP);
    }

    @Nonnull
    protected static String getIdFromPath(@Nonnull Path path) {
        String fileName = path.getFileName().toString();
        return fileName.substring(0, fileName.length() - EXTENSION_LEN);
    }
}

