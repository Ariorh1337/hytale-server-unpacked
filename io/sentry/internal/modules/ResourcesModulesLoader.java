/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.internal.modules;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.internal.modules.ModulesLoader;
import io.sentry.util.ClassLoaderUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class ResourcesModulesLoader
extends ModulesLoader {
    @NotNull
    private final ClassLoader classLoader;

    public ResourcesModulesLoader(@NotNull ILogger logger) {
        this(logger, ResourcesModulesLoader.class.getClassLoader());
    }

    ResourcesModulesLoader(@NotNull ILogger logger, @Nullable ClassLoader classLoader) {
        super(logger);
        this.classLoader = ClassLoaderUtils.classLoaderOrDefault(classLoader);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected Map<String, String> loadModules() {
        TreeMap<String, String> modules = new TreeMap<String, String>();
        try (InputStream resourcesStream = this.classLoader.getResourceAsStream("sentry-external-modules.txt");){
            if (resourcesStream == null) {
                this.logger.log(SentryLevel.INFO, "%s file was not found.", "sentry-external-modules.txt");
                TreeMap<String, String> treeMap = modules;
                return treeMap;
            }
            Map<String, String> map = this.parseStream(resourcesStream);
            return map;
        }
        catch (SecurityException e) {
            this.logger.log(SentryLevel.INFO, "Access to resources denied.", e);
            return modules;
        }
        catch (IOException e) {
            this.logger.log(SentryLevel.INFO, "Access to resources failed.", e);
        }
        return modules;
    }
}

