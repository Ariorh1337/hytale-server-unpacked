/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.SentryAttribute;
import io.sentry.SentryAttributes;
import io.sentry.SentryEvent;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class LoggerPropertiesUtil {
    @ApiStatus.Internal
    public static void applyPropertiesToEvent(@NotNull SentryEvent event, @NotNull List<String> targetKeys, @NotNull Map<String, String> properties, @NotNull String contextName) {
        if (!targetKeys.isEmpty() && !properties.isEmpty()) {
            for (String key : targetKeys) {
                @Nullable String value = properties.remove(key);
                if (value == null) continue;
                event.setTag(key, value);
            }
        }
        if (!properties.isEmpty()) {
            event.getContexts().put(contextName, properties);
        }
    }

    public static void applyPropertiesToEvent(@NotNull SentryEvent event, @NotNull List<String> targetKeys, @NotNull Map<String, String> properties) {
        LoggerPropertiesUtil.applyPropertiesToEvent(event, targetKeys, properties, "MDC");
    }

    @ApiStatus.Internal
    public static void applyPropertiesToAttributes(@NotNull SentryAttributes attributes, @NotNull List<String> targetKeys, @NotNull Map<String, String> properties) {
        if (!targetKeys.isEmpty() && !properties.isEmpty()) {
            for (String key : targetKeys) {
                @Nullable String value = properties.get(key);
                if (value == null) continue;
                attributes.add(SentryAttribute.stringAttribute("mdc." + key, value));
            }
        }
    }
}

