/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.path;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.path.WorldPath;
import com.hypixel.hytale.server.core.universe.world.path.WorldPathChangedEvent;
import com.hypixel.hytale.server.core.util.BsonUtil;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonValue;

public class WorldPathConfig {
    public static final BuilderCodec<WorldPathConfig> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(WorldPathConfig.class, WorldPathConfig::new).addField(new KeyedCodec("Paths", new MapCodec<WorldPath, ConcurrentHashMap>(WorldPath.CODEC, ConcurrentHashMap::new, false)), (config, paths) -> {
        config.paths = paths;
    }, config -> config.paths)).build();
    protected Map<String, WorldPath> paths = new ConcurrentHashMap<String, WorldPath>();

    public WorldPath getPath(String name) {
        return this.paths.get(name);
    }

    @Nonnull
    public Map<String, WorldPath> getPaths() {
        return Collections.unmodifiableMap(this.paths);
    }

    @Nullable
    public WorldPath putPath(@Nonnull WorldPath worldPath) {
        Objects.requireNonNull(worldPath);
        IEventDispatcher<WorldPathChangedEvent, WorldPathChangedEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(WorldPathChangedEvent.class);
        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new WorldPathChangedEvent(worldPath));
        }
        return this.paths.put(worldPath.getName(), worldPath);
    }

    public WorldPath removePath(String path) {
        Objects.requireNonNull(path);
        return this.paths.remove(path);
    }

    @Nonnull
    public CompletableFuture<Void> save(World world) {
        BsonValue bsonValue = CODEC.encode(this);
        return BsonUtil.writeDocument(world.getSavePath().resolve("paths.json"), bsonValue.asDocument());
    }

    @Nonnull
    public static CompletableFuture<WorldPathConfig> load(World world) {
        Path oldPath = world.getSavePath().resolve("paths.bson");
        Path path = world.getSavePath().resolve("paths.json");
        if (Files.exists(oldPath, new LinkOption[0]) && !Files.exists(path, new LinkOption[0])) {
            try {
                Files.move(oldPath, path, new CopyOption[0]);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return CompletableFuture.supplyAsync(() -> {
            WorldPathConfig config = RawJsonReader.readSyncWithBak(path, CODEC, HytaleLogger.getLogger());
            return config != null ? config : new WorldPathConfig();
        });
    }

    @Nonnull
    public String toString() {
        return "WorldPathConfig{paths=" + String.valueOf(this.paths) + "}";
    }
}

