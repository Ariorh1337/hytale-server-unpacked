/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.ISpan;
import io.sentry.protocol.Request;
import java.net.URI;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class UrlUtils {
    @NotNull
    public static final String SENSITIVE_DATA_SUBSTITUTE = "[Filtered]";

    @Nullable
    public static UrlDetails parseNullable(@Nullable String url) {
        return url == null ? null : UrlUtils.parse(url);
    }

    @NotNull
    public static UrlDetails parse(@NotNull String url) {
        try {
            URI uri = new URI(url);
            if (uri.isAbsolute() && !UrlUtils.isValidAbsoluteUrl(uri)) {
                return new UrlDetails(null, null, null);
            }
            @NotNull String schemeAndSeparator = uri.getScheme() == null ? "" : uri.getScheme() + "://";
            @NotNull String authority = uri.getRawAuthority() == null ? "" : uri.getRawAuthority();
            @NotNull String path = uri.getRawPath() == null ? "" : uri.getRawPath();
            @Nullable String query = uri.getRawQuery();
            @Nullable String fragment = uri.getRawFragment();
            @NotNull String filteredUrl = schemeAndSeparator + UrlUtils.filterUserInfo(authority) + path;
            return new UrlDetails(filteredUrl, query, fragment);
        }
        catch (Exception e) {
            return new UrlDetails(null, null, null);
        }
    }

    private static boolean isValidAbsoluteUrl(@NotNull URI uri) {
        try {
            uri.toURL();
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    @NotNull
    private static String filterUserInfo(@NotNull String url) {
        if (!url.contains("@")) {
            return url;
        }
        if (url.startsWith("@")) {
            return SENSITIVE_DATA_SUBSTITUTE + url;
        }
        @NotNull String userInfo = url.substring(0, url.indexOf(64));
        @NotNull String filteredUserInfo = userInfo.contains(":") ? "[Filtered]:[Filtered]" : SENSITIVE_DATA_SUBSTITUTE;
        return filteredUserInfo + url.substring(url.indexOf(64));
    }

    public static final class UrlDetails {
        @Nullable
        private final String url;
        @Nullable
        private final String query;
        @Nullable
        private final String fragment;

        public UrlDetails(@Nullable String url, @Nullable String query, @Nullable String fragment) {
            this.url = url;
            this.query = query;
            this.fragment = fragment;
        }

        @Nullable
        public String getUrl() {
            return this.url;
        }

        @NotNull
        public String getUrlOrFallback() {
            if (this.url == null) {
                return "unknown";
            }
            return this.url;
        }

        @Nullable
        public String getQuery() {
            return this.query;
        }

        @Nullable
        public String getFragment() {
            return this.fragment;
        }

        public void applyToRequest(@Nullable Request request) {
            if (request == null) {
                return;
            }
            request.setUrl(this.url);
            request.setQueryString(this.query);
            request.setFragment(this.fragment);
        }

        public void applyToSpan(@Nullable ISpan span) {
            if (span == null) {
                return;
            }
            if (this.query != null) {
                span.setData("http.query", this.query);
            }
            if (this.fragment != null) {
                span.setData("http.fragment", this.fragment);
            }
        }
    }
}

