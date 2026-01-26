/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.config;

import io.sentry.config.PropertiesProvider;
import io.sentry.util.Objects;
import io.sentry.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractPropertiesProvider
implements PropertiesProvider {
    @NotNull
    private final String prefix;
    @NotNull
    private final Properties properties;

    protected AbstractPropertiesProvider(@NotNull String prefix, @NotNull Properties properties) {
        this.prefix = Objects.requireNonNull(prefix, "prefix is required");
        this.properties = Objects.requireNonNull(properties, "properties are required");
    }

    protected AbstractPropertiesProvider(@NotNull Properties properties) {
        this("", properties);
    }

    @Override
    @Nullable
    public String getProperty(@NotNull String property) {
        return StringUtils.removeSurrounding(this.properties.getProperty(this.prefix + property), "\"");
    }

    @Override
    @NotNull
    public Map<String, String> getMap(@NotNull String property) {
        String prefix = this.prefix + property + ".";
        HashMap<String, String> result = new HashMap<String, String>();
        for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
            String key;
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String) || !(key = (String)entry.getKey()).startsWith(prefix)) continue;
            String value = StringUtils.removeSurrounding((String)entry.getValue(), "\"");
            result.put(key.substring(prefix.length()), value);
        }
        return result;
    }
}

