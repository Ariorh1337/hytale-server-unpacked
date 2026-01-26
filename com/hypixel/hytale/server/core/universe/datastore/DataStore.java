/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.datastore;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DataStore<T> {
    public BuilderCodec<T> getCodec();

    @Nullable
    public T load(String var1) throws IOException;

    public void save(String var1, T var2);

    public void remove(String var1) throws IOException;

    public List<String> list() throws IOException;

    @Nonnull
    default public Map<String, T> loadAll() throws IOException {
        Object2ObjectOpenHashMap<String, T> map = new Object2ObjectOpenHashMap<String, T>();
        for (String id : this.list()) {
            T value = this.load(id);
            if (value == null) continue;
            map.put(id, value);
        }
        return map;
    }

    default public void saveAll(@Nonnull Map<String, T> objectsToSave) {
        for (Map.Entry<String, T> entry : objectsToSave.entrySet()) {
            this.save(entry.getKey(), entry.getValue());
        }
    }

    default public void removeAll() throws IOException {
        for (String id : this.list()) {
            this.remove(id);
        }
    }
}

