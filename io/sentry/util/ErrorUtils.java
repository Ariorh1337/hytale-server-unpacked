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
import io.sentry.SentryEvent;
import io.sentry.protocol.Message;
import java.util.HashSet;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ErrorUtils {
    @ApiStatus.Internal
    public static boolean isIgnored(@Nullable List<FilterString> ignoredErrors, @NotNull SentryEvent event) {
        Throwable throwable;
        if (event == null || ignoredErrors == null || ignoredErrors.isEmpty()) {
            return false;
        }
        @NotNull HashSet<String> possibleMessages = new HashSet<String>();
        @Nullable Message eventMessage = event.getMessage();
        if (eventMessage != null) {
            String formattedMessage;
            @Nullable String stringMessage = eventMessage.getMessage();
            if (stringMessage != null) {
                possibleMessages.add(stringMessage);
            }
            if ((formattedMessage = eventMessage.getFormatted()) != null) {
                possibleMessages.add(formattedMessage);
            }
        }
        if ((throwable = event.getThrowable()) != null) {
            possibleMessages.add(throwable.toString());
        }
        for (FilterString filter : ignoredErrors) {
            if (!possibleMessages.contains(filter.getFilterString())) continue;
            return true;
        }
        for (FilterString filter : ignoredErrors) {
            for (String message : possibleMessages) {
                if (!filter.matches(message)) continue;
                return true;
            }
        }
        return false;
    }
}

