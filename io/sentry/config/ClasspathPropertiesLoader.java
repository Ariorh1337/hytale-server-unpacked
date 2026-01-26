/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.config;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.config.PropertiesLoader;
import io.sentry.util.ClassLoaderUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ClasspathPropertiesLoader
implements PropertiesLoader {
    @NotNull
    private final String fileName;
    @NotNull
    private final ClassLoader classLoader;
    @NotNull
    private final ILogger logger;

    public ClasspathPropertiesLoader(@NotNull String fileName, @Nullable ClassLoader classLoader, @NotNull ILogger logger) {
        this.fileName = fileName;
        this.classLoader = ClassLoaderUtils.classLoaderOrDefault(classLoader);
        this.logger = logger;
    }

    public ClasspathPropertiesLoader(@NotNull ILogger logger) {
        this("sentry.properties", ClasspathPropertiesLoader.class.getClassLoader(), logger);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    @Override
    @Nullable
    public Properties load() {
        block13: {
            try (InputStream inputStream = this.classLoader.getResourceAsStream(this.fileName);){
                Properties properties;
                if (inputStream == null) break block13;
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);){
                    Properties properties2 = new Properties();
                    properties2.load(bufferedInputStream);
                    properties = properties2;
                }
                return properties;
            }
            catch (IOException e) {
                this.logger.log(SentryLevel.ERROR, e, "Failed to load Sentry configuration from classpath resource: %s", this.fileName);
                return null;
            }
        }
        return null;
    }
}

