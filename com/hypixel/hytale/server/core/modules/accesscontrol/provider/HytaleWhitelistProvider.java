/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.accesscontrol.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.AccessProvider;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import javax.annotation.Nonnull;

public class HytaleWhitelistProvider
extends BlockingDiskFile
implements AccessProvider {
    @Nonnull
    private static final String WHITELIST_FILE_PATH = "whitelist.json";
    @Nonnull
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    @Nonnull
    private final Set<UUID> whitelist = new HashSet<UUID>();
    private boolean isEnabled;

    public HytaleWhitelistProvider() {
        super(Paths.get(WHITELIST_FILE_PATH, new String[0]));
    }

    @Override
    protected void read(@Nonnull BufferedReader fileReader) {
        JsonElement element = JsonParser.parseReader(fileReader);
        if (!(element instanceof JsonObject)) {
            throw new JsonParseException("element is not JsonObject!");
        }
        JsonObject jsonObject = (JsonObject)element;
        this.isEnabled = jsonObject.get("enabled").getAsBoolean();
        jsonObject.get("list").getAsJsonArray().forEach(entry -> this.whitelist.add(UUID.fromString(entry.getAsString())));
    }

    @Override
    protected void write(@Nonnull BufferedWriter fileWriter) throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("enabled", this.isEnabled);
        JsonArray jsonArray = new JsonArray();
        for (UUID uuid : this.whitelist) {
            jsonArray.add(uuid.toString());
        }
        jsonObject.add("list", jsonArray);
        fileWriter.write(jsonObject.toString());
    }

    @Override
    protected void create(@Nonnull BufferedWriter fileWriter) throws IOException {
        try (JsonWriter jsonWriter = new JsonWriter(fileWriter);){
            jsonWriter.beginObject().name("enabled").value(false).name("list").beginArray().endArray().endObject();
        }
    }

    @Override
    @Nonnull
    public CompletableFuture<Optional<String>> getDisconnectReason(UUID uuid) {
        this.lock.readLock().lock();
        try {
            if (this.isEnabled && !this.whitelist.contains(uuid)) {
                CompletableFuture<Optional<String>> completableFuture = CompletableFuture.completedFuture(Optional.of("You are not whitelisted!"));
                return completableFuture;
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        return CompletableFuture.completedFuture(Optional.empty());
    }

    public void setEnabled(boolean isEnabled) {
        this.lock.writeLock().lock();
        try {
            this.isEnabled = isEnabled;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public boolean modify(@Nonnull Function<Set<UUID>, Boolean> consumer) {
        boolean result;
        this.lock.writeLock().lock();
        try {
            result = consumer.apply(this.whitelist);
        }
        finally {
            this.lock.writeLock().unlock();
        }
        if (result) {
            this.syncSave();
        }
        return result;
    }

    @Nonnull
    public Set<UUID> getList() {
        this.lock.readLock().lock();
        try {
            Set<UUID> set = Collections.unmodifiableSet(this.whitelist);
            return set;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean isEnabled() {
        this.lock.readLock().lock();
        try {
            boolean bl = this.isEnabled;
            return bl;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
}

