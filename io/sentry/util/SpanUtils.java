/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.FilterString;
import io.sentry.SentryOpenTelemetryMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SpanUtils {
    private static final Map<String, Boolean> ignoredSpanDecisionsCache = new ConcurrentHashMap<String, Boolean>();

    @NotNull
    public static List<String> ignoredSpanOriginsForOpenTelemetry(@NotNull SentryOpenTelemetryMode mode) {
        @NotNull ArrayList<String> origins = new ArrayList<String>();
        if (SentryOpenTelemetryMode.AGENT == mode || SentryOpenTelemetryMode.AGENTLESS_SPRING == mode) {
            origins.add("auto.http.spring_jakarta.webmvc");
            origins.add("auto.http.spring.webmvc");
            origins.add("auto.http.spring7.webmvc");
            origins.add("auto.spring_jakarta.webflux");
            origins.add("auto.spring.webflux");
            origins.add("auto.spring7.webflux");
            origins.add("auto.db.jdbc");
            origins.add("auto.http.spring_jakarta.webclient");
            origins.add("auto.http.spring.webclient");
            origins.add("auto.http.spring7.webclient");
            origins.add("auto.http.spring_jakarta.restclient");
            origins.add("auto.http.spring.restclient");
            origins.add("auto.http.spring7.restclient");
            origins.add("auto.http.spring_jakarta.resttemplate");
            origins.add("auto.http.spring.resttemplate");
            origins.add("auto.http.spring7.resttemplate");
            origins.add("auto.http.openfeign");
            origins.add("auto.http.ktor-client");
        }
        if (SentryOpenTelemetryMode.AGENT == mode) {
            origins.add("auto.graphql.graphql");
            origins.add("auto.graphql.graphql22");
        }
        return origins;
    }

    @ApiStatus.Internal
    public static boolean isIgnored(@Nullable List<FilterString> ignoredOrigins, @Nullable String origin) {
        if (origin == null || ignoredOrigins == null || ignoredOrigins.isEmpty()) {
            return false;
        }
        if (ignoredSpanDecisionsCache.containsKey(origin)) {
            return ignoredSpanDecisionsCache.get(origin);
        }
        for (FilterString ignoredOrigin : ignoredOrigins) {
            if (!ignoredOrigin.getFilterString().equalsIgnoreCase(origin)) continue;
            ignoredSpanDecisionsCache.put(origin, true);
            return true;
        }
        for (FilterString ignoredOrigin : ignoredOrigins) {
            try {
                if (!ignoredOrigin.matches(origin)) continue;
                ignoredSpanDecisionsCache.put(origin, true);
                return true;
            }
            catch (Throwable throwable) {
            }
        }
        ignoredSpanDecisionsCache.put(origin, false);
        return false;
    }
}

