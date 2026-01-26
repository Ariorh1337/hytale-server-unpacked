/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.accesscontrol.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.hypixel.hytale.server.core.modules.accesscontrol.AccessControlModule;
import com.hypixel.hytale.server.core.modules.accesscontrol.ban.Ban;
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.AccessProvider;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nonnull;

public class HytaleBanProvider
extends BlockingDiskFile
implements AccessProvider {
    private final Map<UUID, Ban> bans = new Object2ObjectOpenHashMap<UUID, Ban>();

    public HytaleBanProvider() {
        super(Paths.get("bans.json", new String[0]));
    }

    @Override
    @Nonnull
    public CompletableFuture<Optional<String>> getDisconnectReason(UUID uuid) {
        Ban ban = this.bans.get(uuid);
        if (ban != null && !ban.isInEffect()) {
            this.bans.remove(uuid);
            ban = null;
        }
        if (ban != null) {
            return ban.getDisconnectReason(uuid);
        }
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    protected void read(@Nonnull BufferedReader fileReader) {
        JsonParser.parseReader(fileReader).getAsJsonArray().forEach(entry -> {
            JsonObject jsonObject = entry.getAsJsonObject();
            try {
                Ban ban = AccessControlModule.get().parseBan(jsonObject.get("type").getAsString(), jsonObject);
                Objects.requireNonNull(ban.getBy(), "Ban has null getBy");
                Objects.requireNonNull(ban.getTarget(), "Ban has null getTarget");
                if (ban.isInEffect()) {
                    this.bans.put(ban.getTarget(), ban);
                }
            }
            catch (Exception ex) {
                throw new RuntimeException("Failed to parse ban!", ex);
            }
        });
    }

    @Override
    protected void write(@Nonnull BufferedWriter fileWriter) throws IOException {
        JsonArray array = new JsonArray();
        this.bans.forEach((key, value) -> array.add(value.toJsonObject()));
        fileWriter.write(array.toString());
    }

    @Override
    protected void create(@Nonnull BufferedWriter fileWriter) throws IOException {
        try (JsonWriter jsonWriter = new JsonWriter(fileWriter);){
            jsonWriter.beginArray().endArray();
        }
    }

    public boolean hasBan(UUID uuid) {
        this.fileLock.readLock().lock();
        try {
            boolean bl = this.bans.containsKey(uuid);
            return bl;
        }
        finally {
            this.fileLock.readLock().unlock();
        }
    }

    public boolean modify(@Nonnull Function<Map<UUID, Ban>, Boolean> function) {
        boolean modified;
        this.fileLock.writeLock().lock();
        try {
            modified = function.apply(this.bans);
        }
        finally {
            this.fileLock.writeLock().unlock();
        }
        if (modified) {
            this.syncSave();
        }
        return modified;
    }
}

