/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.EventProcessor;
import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.UncaughtExceptionHandlerIntegration;
import io.sentry.hints.EventDropReason;
import io.sentry.protocol.SentryException;
import io.sentry.util.HintUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DeduplicateMultithreadedEventProcessor
implements EventProcessor {
    @NotNull
    private final Map<String, Long> processedEvents = Collections.synchronizedMap(new HashMap());
    @NotNull
    private final SentryOptions options;

    public DeduplicateMultithreadedEventProcessor(@NotNull SentryOptions options) {
        this.options = options;
    }

    @Override
    @Nullable
    public SentryEvent process(@NotNull SentryEvent event, @NotNull Hint hint) {
        if (!HintUtils.hasType(hint, UncaughtExceptionHandlerIntegration.UncaughtExceptionHint.class)) {
            return event;
        }
        SentryException exception = event.getUnhandledException();
        if (exception == null) {
            return event;
        }
        String type = exception.getType();
        if (type == null) {
            return event;
        }
        Long currentEventTid = exception.getThreadId();
        if (currentEventTid == null) {
            return event;
        }
        Long tid = this.processedEvents.get(type);
        if (tid != null && !tid.equals(currentEventTid)) {
            this.options.getLogger().log(SentryLevel.INFO, "Event %s has been dropped due to multi-threaded deduplication", event.getEventId());
            HintUtils.setEventDropReason(hint, EventDropReason.MULTITHREADED_DEDUPLICATION);
            return null;
        }
        this.processedEvents.put(type, currentEventTid);
        return event;
    }

    @Override
    @Nullable
    public Long getOrder() {
        return 7000L;
    }
}

