/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.internal.debugmeta;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.internal.debugmeta.IDebugMetaLoader;
import io.sentry.util.ClassLoaderUtils;
import io.sentry.util.DebugMetaPropertiesApplier;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class ResourcesDebugMetaLoader
implements IDebugMetaLoader {
    @NotNull
    private final ILogger logger;
    @NotNull
    private final ClassLoader classLoader;

    public ResourcesDebugMetaLoader(@NotNull ILogger logger) {
        this(logger, ResourcesDebugMetaLoader.class.getClassLoader());
    }

    ResourcesDebugMetaLoader(@NotNull ILogger logger, @Nullable ClassLoader classLoader) {
        this.logger = logger;
        this.classLoader = ClassLoaderUtils.classLoaderOrDefault(classLoader);
    }

    @Override
    @Nullable
    public List<Properties> loadDebugMeta() {
        @NotNull ArrayList<Properties> debugPropertyList = new ArrayList<Properties>();
        try {
            @NotNull Enumeration<URL> resourceUrls = this.classLoader.getResources(DebugMetaPropertiesApplier.DEBUG_META_PROPERTIES_FILENAME);
            while (resourceUrls.hasMoreElements()) {
                @NotNull URL currentUrl = resourceUrls.nextElement();
                try {
                    InputStream is = currentUrl.openStream();
                    try {
                        @NotNull Properties currentProperties = new Properties();
                        currentProperties.load(is);
                        debugPropertyList.add(currentProperties);
                        this.logger.log(SentryLevel.INFO, "Debug Meta Data Properties loaded from %s", currentUrl);
                    }
                    finally {
                        if (is == null) continue;
                        is.close();
                    }
                }
                catch (RuntimeException e) {
                    this.logger.log(SentryLevel.ERROR, e, "%s file is malformed.", currentUrl);
                }
            }
        }
        catch (IOException e) {
            this.logger.log(SentryLevel.ERROR, e, "Failed to load %s", DebugMetaPropertiesApplier.DEBUG_META_PROPERTIES_FILENAME);
        }
        if (debugPropertyList.isEmpty()) {
            this.logger.log(SentryLevel.INFO, "No %s file was found.", DebugMetaPropertiesApplier.DEBUG_META_PROPERTIES_FILENAME);
            return null;
        }
        return debugPropertyList;
    }
}

