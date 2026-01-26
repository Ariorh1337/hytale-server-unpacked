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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FilesystemPropertiesLoader
implements PropertiesLoader {
    @NotNull
    private final String filePath;
    @NotNull
    private final ILogger logger;
    private boolean logNonExisting;

    public FilesystemPropertiesLoader(@NotNull String filePath, @NotNull ILogger logger) {
        this(filePath, logger, true);
    }

    public FilesystemPropertiesLoader(@NotNull String filePath, @NotNull ILogger logger, boolean logNonExisting) {
        this.filePath = filePath;
        this.logger = logger;
        this.logNonExisting = logNonExisting;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    @Nullable
    public Properties load() {
        try {
            File f = new File(this.filePath.trim());
            if (f.isFile() && f.canRead()) {
                try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(f));){
                    Properties properties = new Properties();
                    properties.load(is);
                    Properties properties2 = properties;
                    return properties2;
                }
            }
            if (!f.isFile()) {
                if (!this.logNonExisting) return null;
                this.logger.log(SentryLevel.ERROR, "Failed to load Sentry configuration since it is not a file or does not exist: %s", this.filePath);
                return null;
            }
            if (f.canRead()) return null;
            this.logger.log(SentryLevel.ERROR, "Failed to load Sentry configuration since it is not readable: %s", this.filePath);
            return null;
        }
        catch (Throwable e) {
            this.logger.log(SentryLevel.ERROR, e, "Failed to load Sentry configuration from file: %s", this.filePath);
            return null;
        }
    }
}

