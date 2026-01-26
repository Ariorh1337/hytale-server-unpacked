/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.util;

import java.net.URI;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class PropagationTargetsUtils {
    public static boolean contain(@NotNull List<String> origins, @NotNull String url) {
        if (origins.isEmpty()) {
            return false;
        }
        for (String origin : origins) {
            if (url.contains(origin)) {
                return true;
            }
            try {
                if (!url.matches(origin)) continue;
                return true;
            }
            catch (Exception exception) {
            }
        }
        return false;
    }

    public static boolean contain(@NotNull List<String> origins, URI uri) {
        return PropagationTargetsUtils.contain(origins, uri.toString());
    }
}

