/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.SentryAttribute;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryAttributes {
    @NotNull
    private final Map<String, SentryAttribute> attributes;

    private SentryAttributes(@NotNull Map<String, SentryAttribute> attributes) {
        this.attributes = attributes;
    }

    public void add(@Nullable SentryAttribute attribute) {
        if (attribute == null) {
            return;
        }
        this.attributes.put(attribute.getName(), attribute);
    }

    @NotNull
    public Map<String, SentryAttribute> getAttributes() {
        return this.attributes;
    }

    @NotNull
    public static SentryAttributes of(SentryAttribute ... attributes) {
        if (attributes == null) {
            return new SentryAttributes(new ConcurrentHashMap<String, SentryAttribute>());
        }
        @NotNull SentryAttributes sentryAttributes = new SentryAttributes(new ConcurrentHashMap<String, SentryAttribute>(attributes.length));
        for (SentryAttribute attribute : attributes) {
            sentryAttributes.add(attribute);
        }
        return sentryAttributes;
    }

    @NotNull
    public static SentryAttributes fromMap(@Nullable Map<String, Object> attributes) {
        if (attributes == null) {
            return new SentryAttributes(new ConcurrentHashMap<String, SentryAttribute>());
        }
        @NotNull SentryAttributes sentryAttributes = new SentryAttributes(new ConcurrentHashMap<String, SentryAttribute>(attributes.size()));
        for (Map.Entry<String, Object> attribute : attributes.entrySet()) {
            @Nullable String key = attribute.getKey();
            if (key == null) continue;
            sentryAttributes.add(SentryAttribute.named(key, attribute.getValue()));
        }
        return sentryAttributes;
    }
}

