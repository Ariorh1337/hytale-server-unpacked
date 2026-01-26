/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.internal.modules;

import io.sentry.ILogger;
import io.sentry.internal.modules.IModulesLoader;
import io.sentry.internal.modules.ModulesLoader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
@ApiStatus.Internal
public final class CompositeModulesLoader
extends ModulesLoader {
    private final List<IModulesLoader> loaders;

    public CompositeModulesLoader(@NotNull List<IModulesLoader> loaders, @NotNull ILogger logger) {
        super(logger);
        this.loaders = loaders;
    }

    @Override
    protected Map<String, String> loadModules() {
        @NotNull TreeMap<String, String> allModules = new TreeMap<String, String>();
        for (IModulesLoader loader : this.loaders) {
            @Nullable Map<String, String> modules = loader.getOrLoadModules();
            if (modules == null) continue;
            allModules.putAll(modules);
        }
        return allModules;
    }
}

