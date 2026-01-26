/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.config;

import io.sentry.config.PropertiesProvider;
import io.sentry.util.StringUtils;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class EnvironmentVariablePropertiesProvider
implements PropertiesProvider {
    private static final String PREFIX = "SENTRY";

    EnvironmentVariablePropertiesProvider() {
    }

    @Override
    @Nullable
    public String getProperty(@NotNull String property) {
        return StringUtils.removeSurrounding(System.getenv(this.propertyToEnvironmentVariableName(property)), "\"");
    }

    @Override
    @NotNull
    public Map<String, String> getMap(@NotNull String property) {
        String prefix = this.propertyToEnvironmentVariableName(property) + "_";
        ConcurrentHashMap<String, @NotNull String> result = new ConcurrentHashMap<String, String>();
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            String value;
            String key = entry.getKey();
            if (!key.startsWith(prefix) || (value = StringUtils.removeSurrounding(entry.getValue(), "\"")) == null) continue;
            result.put(key.substring(prefix.length()).toLowerCase(Locale.ROOT), value);
        }
        return result;
    }

    @NotNull
    private String propertyToEnvironmentVariableName(@NotNull String property) {
        return "SENTRY_" + property.replace(".", "_").replace("-", "_").toUpperCase(Locale.ROOT);
    }
}

