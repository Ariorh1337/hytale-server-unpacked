/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.hypixel.hytale.procedurallib.json.Loader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class JsonLoader<K extends SeedResource, T>
extends Loader<K, T> {
    @Nullable
    protected final JsonElement json;

    public JsonLoader(SeedString<K> seed, Path dataFolder, @Nullable JsonElement json) {
        super(seed, dataFolder);
        this.json = json != null && json.isJsonObject() && json.getAsJsonObject().has("File") ? this.loadFileConstructor(json.getAsJsonObject().get("File").getAsString()) : json;
    }

    public boolean has(String name) {
        return this.json != null && this.json.isJsonObject() && this.json.getAsJsonObject().has(name);
    }

    @Nonnull
    public JsonElement getOrLoad(@Nonnull JsonElement element) {
        JsonObject obj;
        JsonElement path;
        if (element.isJsonObject() && (path = (obj = element.getAsJsonObject()).get("File")) != null && path.isJsonPrimitive() && path.getAsJsonPrimitive().isString()) {
            JsonElement loaded = this.loadFileElem(path.getAsString());
            element = Objects.requireNonNullElse(loaded, element);
        }
        return element;
    }

    @Nullable
    public JsonElement get(String name) {
        if (this.json == null || !this.json.isJsonObject()) {
            return null;
        }
        JsonElement element = this.json.getAsJsonObject().get(name);
        if (element != null && element.isJsonObject()) {
            element = this.getOrLoad(element);
        }
        return element;
    }

    @Nullable
    public JsonElement getRaw(String name) {
        if (this.json == null || !this.json.isJsonObject()) {
            return null;
        }
        return this.json.getAsJsonObject().get(name);
    }

    protected JsonElement loadFile(@Nonnull String filePath) {
        JsonElement jsonElement;
        Path file = this.dataFolder.resolve(filePath.replace('.', File.separatorChar) + ".json");
        JsonReader reader = new JsonReader(Files.newBufferedReader(file));
        try {
            jsonElement = JsonParser.parseReader(reader);
        }
        catch (Throwable throwable) {
            try {
                try {
                    reader.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Throwable e) {
                throw new Error("Error while loading file reference." + file.toString(), e);
            }
        }
        reader.close();
        return jsonElement;
    }

    protected JsonElement loadFileElem(@Nonnull String filePath) {
        return this.loadFile(filePath);
    }

    protected JsonElement loadFileConstructor(@Nonnull String filePath) {
        return this.loadFile(filePath);
    }

    @Nonnull
    protected JsonObject mustGetObject(@Nonnull String key, @Nullable JsonObject defaultValue) {
        return this.mustGet(key, defaultValue, JsonObject.class, JsonElement::isJsonObject, JsonElement::getAsJsonObject);
    }

    @Nonnull
    protected JsonArray mustGetArray(@Nonnull String key, @Nullable JsonArray defaultValue) {
        return this.mustGet(key, defaultValue, JsonArray.class, JsonElement::isJsonArray, JsonElement::getAsJsonArray);
    }

    @Nonnull
    protected String mustGetString(@Nonnull String key, @Nullable String defaultValue) {
        return this.mustGet(key, defaultValue, String.class, JsonLoader::isString, JsonElement::getAsString);
    }

    @Nonnull
    protected Boolean mustGetBool(@Nonnull String key, @Nullable Boolean defaultValue) {
        return this.mustGet(key, defaultValue, Boolean.class, JsonLoader::isBoolean, JsonElement::getAsBoolean);
    }

    @Nonnull
    protected Number mustGetNumber(@Nonnull String key, @Nullable Number defaultValue) {
        return this.mustGet(key, defaultValue, Number.class, JsonLoader::isNumber, JsonElement::getAsNumber);
    }

    protected <V> V mustGet(@Nonnull String key, @Nullable V defaultValue, @Nonnull Class<V> type, @Nonnull Predicate<JsonElement> predicate, @Nonnull Function<JsonElement, V> mapper) {
        return JsonLoader.mustGet(key, this.get(key), defaultValue, type, predicate, mapper);
    }

    protected static <V> V mustGet(@Nonnull String key, @Nullable JsonElement element, @Nullable V defaultValue, @Nonnull Class<V> type, @Nonnull Predicate<JsonElement> predicate, @Nonnull Function<JsonElement, V> mapper) {
        if (element == null) {
            if (defaultValue != null) {
                return defaultValue;
            }
            throw JsonLoader.error("Missing property '%s'", key);
        }
        if (!predicate.test(element)) {
            throw JsonLoader.error("Property '%s' must be of type '%s'", key, type.getSimpleName());
        }
        return mapper.apply(element);
    }

    protected static Error error(String format, Object ... args) {
        return new Error(String.format(format, args));
    }

    protected static Error error(Throwable parent, String format, Object ... args) {
        return new Error(String.format(format, args), parent);
    }

    private static boolean isString(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }

    protected static boolean isNumber(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
    }

    protected static boolean isBoolean(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean();
    }

    public static interface Constants {
        public static final char JSON_FILEPATH_SEPARATOR = '.';
        public static final String KEY_FILE = "File";
    }
}

