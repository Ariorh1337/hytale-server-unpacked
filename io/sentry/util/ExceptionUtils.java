/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.util;

import io.sentry.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class ExceptionUtils {
    @NotNull
    public static Throwable findRootCause(@NotNull Throwable throwable) {
        Throwable rootCause;
        Objects.requireNonNull(throwable, "throwable cannot be null");
        for (rootCause = throwable; rootCause.getCause() != null && rootCause.getCause() != rootCause; rootCause = rootCause.getCause()) {
        }
        return rootCause;
    }

    @ApiStatus.Internal
    public static boolean isIgnored(@NotNull Set<Class<? extends Throwable>> ignoredExceptionsForType, @NotNull Throwable throwable) {
        return ignoredExceptionsForType.contains(throwable.getClass());
    }
}

