/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PropertiesProvider {
    @Nullable
    public String getProperty(@NotNull String var1);

    @NotNull
    public Map<String, String> getMap(@NotNull String var1);

    @NotNull
    default public List<String> getList(@NotNull String property) {
        String value = this.getProperty(property);
        return value != null ? Arrays.asList(value.split(",")) : Collections.emptyList();
    }

    @Nullable
    default public List<String> getListOrNull(@NotNull String property) {
        String value = this.getProperty(property);
        return value != null ? Arrays.asList(value.split(",")) : null;
    }

    @NotNull
    default public String getProperty(@NotNull String property, @NotNull String defaultValue) {
        String result = this.getProperty(property);
        return result != null ? result : defaultValue;
    }

    @Nullable
    default public Boolean getBooleanProperty(@NotNull String property) {
        String result = this.getProperty(property);
        return result != null ? Boolean.valueOf(result) : null;
    }

    @Nullable
    default public Double getDoubleProperty(@NotNull String property) {
        String prop = this.getProperty(property);
        Double result = null;
        if (prop != null) {
            try {
                result = Double.valueOf(prop);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return result;
    }

    @Nullable
    default public Long getLongProperty(@NotNull String property) {
        String prop = this.getProperty(property);
        Long result = null;
        if (prop != null) {
            try {
                result = Long.valueOf(prop);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return result;
    }
}

