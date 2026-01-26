/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.cache;

import io.sentry.JsonDeserializer;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CacheUtils {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    CacheUtils() {
    }

    static <T> void store(@NotNull SentryOptions options, @NotNull T entity, @NotNull String dirName, @NotNull String fileName) {
        File cacheDir = CacheUtils.ensureCacheDir(options, dirName);
        if (cacheDir == null) {
            options.getLogger().log(SentryLevel.INFO, "Cache dir is not set, cannot store in scope cache", new Object[0]);
            return;
        }
        File file = new File(cacheDir, fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)outputStream, UTF_8));){
            options.getSerializer().serialize(entity, writer);
        }
        catch (Throwable e) {
            options.getLogger().log(SentryLevel.ERROR, e, "Error persisting entity: %s", fileName);
        }
    }

    static void delete(@NotNull SentryOptions options, @NotNull String dirName, @NotNull String fileName) {
        File cacheDir = CacheUtils.ensureCacheDir(options, dirName);
        if (cacheDir == null) {
            options.getLogger().log(SentryLevel.INFO, "Cache dir is not set, cannot delete from scope cache", new Object[0]);
            return;
        }
        File file = new File(cacheDir, fileName);
        options.getLogger().log(SentryLevel.DEBUG, "Deleting %s from scope cache", fileName);
        if (!file.delete()) {
            options.getLogger().log(SentryLevel.INFO, "Failed to delete: %s", file.getAbsolutePath());
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    static <T, R> T read(@NotNull SentryOptions options, @NotNull String dirName, @NotNull String fileName, @NotNull Class<T> clazz, @Nullable JsonDeserializer<R> elementDeserializer) {
        File cacheDir = CacheUtils.ensureCacheDir(options, dirName);
        if (cacheDir == null) {
            options.getLogger().log(SentryLevel.INFO, "Cache dir is not set, cannot read from scope cache", new Object[0]);
            return null;
        }
        File file = new File(cacheDir, fileName);
        if (!file.exists()) {
            options.getLogger().log(SentryLevel.DEBUG, "No entry stored for %s", fileName);
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(file), UTF_8));){
            if (elementDeserializer == null) {
                T t = options.getSerializer().deserialize(reader, clazz);
                return t;
            }
            T t = options.getSerializer().deserializeCollection(reader, clazz, elementDeserializer);
            return t;
        }
        catch (Throwable e) {
            options.getLogger().log(SentryLevel.ERROR, e, "Error reading entity from scope cache: %s", fileName);
            return null;
        }
    }

    @Nullable
    static File ensureCacheDir(@NotNull SentryOptions options, @NotNull String cacheDirName) {
        String cacheDir = options.getCacheDirPath();
        if (cacheDir == null) {
            return null;
        }
        File dir = new File(cacheDir, cacheDirName);
        dir.mkdirs();
        return dir;
    }
}

