/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.Dsn;
import io.sentry.SentryOptions;
import java.net.URI;
import java.util.Locale;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class DsnUtil {
    public static boolean urlContainsDsnHost(@Nullable SentryOptions options, @Nullable String url) {
        if (options == null) {
            return false;
        }
        if (url == null) {
            return false;
        }
        @Nullable String dsnString = options.getDsn();
        if (dsnString == null) {
            return false;
        }
        @NotNull Dsn dsn = options.retrieveParsedDsn();
        @NotNull URI sentryUri = dsn.getSentryUri();
        @Nullable String dsnHost = sentryUri.getHost();
        if (dsnHost == null) {
            return false;
        }
        @NotNull String lowerCaseHost = dsnHost.toLowerCase(Locale.ROOT);
        int dsnPort = sentryUri.getPort();
        if (dsnPort > 0) {
            return url.toLowerCase(Locale.ROOT).contains(lowerCaseHost + ":" + dsnPort);
        }
        return url.toLowerCase(Locale.ROOT).contains(lowerCaseHost);
    }
}

