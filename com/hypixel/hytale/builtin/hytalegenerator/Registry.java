/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;

public class Registry<T> {
    private Map<T, Integer> objectToId = new HashMap<T, Integer>();
    private Map<Integer, T> idToObject = new HashMap<Integer, T>();

    public int getIdOrRegister(T object) {
        Integer id = this.objectToId.get(object);
        if (id != null) {
            return id;
        }
        id = this.objectToId.size();
        this.idToObject.put(id, object);
        this.objectToId.put(object, id);
        return id;
    }

    public T getObject(int id) {
        return this.idToObject.get(id);
    }

    public int size() {
        return this.objectToId.size();
    }

    @Nonnull
    public List<T> getAllValues() {
        return new ArrayList<T>(this.idToObject.values());
    }

    public void forEach(@Nonnull BiConsumer<Integer, T> consumer) {
        this.idToObject.forEach(consumer::accept);
    }
}

