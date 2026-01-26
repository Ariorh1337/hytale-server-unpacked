/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryLogEvent;
import io.sentry.SentryReplayEvent;
import io.sentry.protocol.SentryTransaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EventProcessor {
    @Nullable
    default public SentryEvent process(@NotNull SentryEvent event, @NotNull Hint hint) {
        return event;
    }

    @Nullable
    default public SentryTransaction process(@NotNull SentryTransaction transaction, @NotNull Hint hint) {
        return transaction;
    }

    @Nullable
    default public SentryReplayEvent process(@NotNull SentryReplayEvent event, @NotNull Hint hint) {
        return event;
    }

    @Nullable
    default public SentryLogEvent process(@NotNull SentryLogEvent event) {
        return event;
    }

    @Nullable
    default public Long getOrder() {
        return null;
    }
}

