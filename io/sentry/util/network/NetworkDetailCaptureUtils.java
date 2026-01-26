/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.VisibleForTesting
 */
package io.sentry.util.network;

import io.sentry.util.network.NetworkBody;
import io.sentry.util.network.NetworkRequestData;
import io.sentry.util.network.ReplayNetworkRequestOrResponse;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public final class NetworkDetailCaptureUtils {
    private NetworkDetailCaptureUtils() {
    }

    @Nullable
    public static NetworkRequestData initializeForUrl(@NotNull String url, @Nullable String method, @Nullable List<String> networkDetailAllowUrls, @Nullable List<String> networkDetailDenyUrls) {
        if (!NetworkDetailCaptureUtils.shouldCaptureUrl(url, networkDetailAllowUrls, networkDetailDenyUrls)) {
            return null;
        }
        return new NetworkRequestData(method);
    }

    @NotNull
    public static <T> ReplayNetworkRequestOrResponse createRequest(@NotNull T httpObject, @Nullable Long bodySize, boolean networkCaptureBodies, @NotNull NetworkBodyExtractor<T> bodyExtractor, @NotNull List<String> networkRequestHeaders, @NotNull NetworkHeaderExtractor<T> headerExtractor) {
        return NetworkDetailCaptureUtils.createRequestOrResponseInternal(httpObject, bodySize, networkCaptureBodies, bodyExtractor, networkRequestHeaders, headerExtractor);
    }

    @NotNull
    public static <T> ReplayNetworkRequestOrResponse createResponse(@NotNull T httpObject, @Nullable Long bodySize, boolean networkCaptureBodies, @NotNull NetworkBodyExtractor<T> bodyExtractor, @NotNull List<String> networkResponseHeaders, @NotNull NetworkHeaderExtractor<T> headerExtractor) {
        return NetworkDetailCaptureUtils.createRequestOrResponseInternal(httpObject, bodySize, networkCaptureBodies, bodyExtractor, networkResponseHeaders, headerExtractor);
    }

    private static boolean shouldCaptureUrl(@NotNull String url, @Nullable List<String> networkDetailAllowUrls, @Nullable List<String> networkDetailDenyUrls) {
        if (networkDetailDenyUrls != null) {
            for (String pattern : networkDetailDenyUrls) {
                if (pattern == null || !url.matches(pattern)) continue;
                return false;
            }
        }
        if (networkDetailAllowUrls == null) {
            return false;
        }
        for (String pattern : networkDetailAllowUrls) {
            if (pattern == null || !url.matches(pattern)) continue;
            return true;
        }
        return false;
    }

    @VisibleForTesting
    @NotNull
    static Map<String, String> getCaptureHeaders(@Nullable Map<String, String> allHeaders, @NotNull List<String> allowedHeaders) {
        LinkedHashMap<String, String> capturedHeaders = new LinkedHashMap<String, String>();
        if (allHeaders == null) {
            return capturedHeaders;
        }
        HashSet<String> normalizedAllowed = new HashSet<String>();
        for (String string : allowedHeaders) {
            if (string == null) continue;
            normalizedAllowed.add(string.toLowerCase(Locale.ROOT));
        }
        for (Map.Entry entry : allHeaders.entrySet()) {
            if (!normalizedAllowed.contains(((String)entry.getKey()).toLowerCase(Locale.ROOT))) continue;
            capturedHeaders.put((String)entry.getKey(), (String)entry.getValue());
        }
        return capturedHeaders;
    }

    @NotNull
    private static <T> ReplayNetworkRequestOrResponse createRequestOrResponseInternal(@NotNull T httpObject, @Nullable Long bodySize, boolean networkCaptureBodies, @NotNull NetworkBodyExtractor<T> bodyExtractor, @NotNull List<String> allowedHeaders, @NotNull NetworkHeaderExtractor<T> headerExtractor) {
        NetworkBody body = null;
        if (networkCaptureBodies) {
            body = bodyExtractor.extract(httpObject);
        }
        Map<String, String> headers = NetworkDetailCaptureUtils.getCaptureHeaders(headerExtractor.extract(httpObject), allowedHeaders);
        return new ReplayNetworkRequestOrResponse(bodySize, body, headers);
    }

    public static interface NetworkBodyExtractor<T> {
        @Nullable
        public NetworkBody extract(@NotNull T var1);
    }

    public static interface NetworkHeaderExtractor<T> {
        @NotNull
        public Map<String, String> extract(@NotNull T var1);
    }
}

