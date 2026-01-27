/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class ServiceHttpClientFactory {
    private ServiceHttpClientFactory() {
    }

    @Nonnull
    public static HttpClient.Builder newBuilder(@Nonnull Duration connectTimeout) {
        Objects.requireNonNull(connectTimeout, "connectTimeout");
        HttpClient.Builder builder = HttpClient.newBuilder().connectTimeout(connectTimeout);
        return builder;
    }

    @Nonnull
    public static HttpClient create(@Nonnull Duration connectTimeout) {
        return ServiceHttpClientFactory.newBuilder(connectTimeout).build();
    }
}

