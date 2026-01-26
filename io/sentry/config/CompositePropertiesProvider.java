/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.config;

import io.sentry.config.PropertiesProvider;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CompositePropertiesProvider
implements PropertiesProvider {
    @NotNull
    private final List<PropertiesProvider> providers;

    public CompositePropertiesProvider(@NotNull List<PropertiesProvider> providers) {
        this.providers = providers;
    }

    @Override
    @Nullable
    public String getProperty(@NotNull String property) {
        for (PropertiesProvider provider : this.providers) {
            String result = provider.getProperty(property);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    @Override
    @NotNull
    public Map<String, String> getMap(@NotNull String property) {
        ConcurrentHashMap<String, String> result = new ConcurrentHashMap<String, String>();
        for (PropertiesProvider provider : this.providers) {
            result.putAll(provider.getMap(property));
        }
        return result;
    }
}

