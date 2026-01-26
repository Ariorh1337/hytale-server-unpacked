/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SubTypeTypeAdapterFactory
implements TypeAdapterFactory {
    private final Class<?> baseClassType;
    private final String typeFieldName;
    private final Map<Class<?>, String> classToName = new HashMap();

    private SubTypeTypeAdapterFactory(Class<?> baseClassType, String typeFieldName) {
        this.baseClassType = baseClassType;
        this.typeFieldName = typeFieldName;
    }

    @Nonnull
    public static SubTypeTypeAdapterFactory of(Class<?> baseClass, String typeFieldName) {
        return new SubTypeTypeAdapterFactory(baseClass, typeFieldName);
    }

    @Nonnull
    public SubTypeTypeAdapterFactory registerSubType(Class<?> clazz, String name) {
        if (this.classToName.containsKey(clazz)) {
            throw new IllegalArgumentException();
        }
        if (this.classToName.containsValue(name)) {
            throw new IllegalArgumentException();
        }
        this.classToName.put(clazz, name);
        return this;
    }

    @Override
    @Nullable
    public <T> TypeAdapter<T> create(@Nonnull Gson gson, @Nonnull TypeToken<T> type) {
        if (type.getRawType() != this.baseClassType) {
            return null;
        }
        final HashMap delegateMap = new HashMap();
        this.classToName.forEach((aClass, name) -> delegateMap.put(aClass, Map.entry(name, gson.getDelegateAdapter(this, TypeToken.get(aClass)))));
        return new TypeAdapter<T>(this){
            final /* synthetic */ SubTypeTypeAdapterFactory this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void write(JsonWriter out, @Nonnull T value) throws IOException {
                Map.Entry entry = (Map.Entry)delegateMap.get(value.getClass());
                if (entry == null) {
                    throw new IllegalArgumentException();
                }
                JsonObject result = new JsonObject();
                JsonObject obj = ((TypeAdapter)entry.getValue()).toJsonTree(value).getAsJsonObject();
                result.addProperty(this.this$0.typeFieldName, (String)entry.getKey());
                obj.entrySet().forEach(stringJsonElementEntry -> result.add((String)stringJsonElementEntry.getKey(), (JsonElement)stringJsonElementEntry.getValue()));
                Streams.write(result, out);
            }

            @Override
            public T read(JsonReader in) {
                throw new RuntimeException("Unsupported");
            }
        }.nullSafe();
    }
}

