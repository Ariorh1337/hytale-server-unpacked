/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.Breadcrumb;
import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.protocol.SentryException;
import io.sentry.protocol.SentryStackFrame;
import io.sentry.protocol.SentryStackTrace;
import io.sentry.protocol.SentryThread;
import io.sentry.util.JsonSerializationUtils;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class EventSizeLimitingUtils {
    private static final long MAX_EVENT_SIZE_BYTES = 0x100000L;
    private static final int MAX_FRAMES_PER_STACK = 500;
    private static final int FRAMES_PER_SIDE = 250;

    private EventSizeLimitingUtils() {
    }

    @Nullable
    public static SentryEvent limitEventSize(@NotNull SentryEvent event, @NotNull Hint hint, @NotNull SentryOptions options) {
        try {
            if (!options.isEnableEventSizeLimiting()) {
                return event;
            }
            if (EventSizeLimitingUtils.isSizeOk(event, options)) {
                return event;
            }
            options.getLogger().log(SentryLevel.INFO, "Event %s exceeds %d bytes limit. Reducing size by dropping fields.", event.getEventId(), 0x100000L);
            @NotNull SentryEvent reducedEvent = event;
            @Nullable SentryOptions.OnOversizedEventCallback callback = options.getOnOversizedEvent();
            if (callback != null) {
                try {
                    reducedEvent = callback.execute(reducedEvent, hint);
                    if (EventSizeLimitingUtils.isSizeOk(reducedEvent, options)) {
                        return reducedEvent;
                    }
                }
                catch (Throwable e) {
                    options.getLogger().log(SentryLevel.ERROR, "The onOversizedEvent callback threw an exception. It will be ignored and automatic reduction will continue.", e);
                    reducedEvent = event;
                }
            }
            if (EventSizeLimitingUtils.isSizeOk(reducedEvent = EventSizeLimitingUtils.removeAllBreadcrumbs(reducedEvent, options), options)) {
                return reducedEvent;
            }
            if (!EventSizeLimitingUtils.isSizeOk(reducedEvent = EventSizeLimitingUtils.truncateStackFrames(reducedEvent, options), options)) {
                options.getLogger().log(SentryLevel.WARNING, "Event %s still exceeds size limit after reducing all fields. Event may be rejected by server.", event.getEventId());
            }
            return reducedEvent;
        }
        catch (Throwable e) {
            options.getLogger().log(SentryLevel.ERROR, "An error occurred while limiting event size. Event will be sent as-is.", e);
            return event;
        }
    }

    private static boolean isSizeOk(@NotNull SentryEvent event, @NotNull SentryOptions options) {
        long size = JsonSerializationUtils.byteSizeOf(options.getSerializer(), options.getLogger(), event);
        return size <= 0x100000L;
    }

    @NotNull
    private static SentryEvent removeAllBreadcrumbs(@NotNull SentryEvent event, @NotNull SentryOptions options) {
        @Nullable List<Breadcrumb> breadcrumbs = event.getBreadcrumbs();
        if (breadcrumbs != null && !breadcrumbs.isEmpty()) {
            event.setBreadcrumbs(null);
            options.getLogger().log(SentryLevel.DEBUG, "Removed breadcrumbs to reduce size of event %s", event.getEventId());
        }
        return event;
    }

    @NotNull
    private static SentryEvent truncateStackFrames(@NotNull SentryEvent event, @NotNull SentryOptions options) {
        List<SentryThread> threads;
        @Nullable List<SentryException> exceptions = event.getExceptions();
        if (exceptions != null) {
            for (SentryException exception : exceptions) {
                @Nullable SentryStackTrace stacktrace = exception.getStacktrace();
                if (stacktrace == null) continue;
                EventSizeLimitingUtils.truncateStackFramesInStackTrace(stacktrace, event, options, "Truncated exception stack frames of event %s");
            }
        }
        if ((threads = event.getThreads()) != null) {
            for (SentryThread thread : threads) {
                @Nullable SentryStackTrace stacktrace = thread.getStacktrace();
                if (stacktrace == null) continue;
                EventSizeLimitingUtils.truncateStackFramesInStackTrace(stacktrace, event, options, "Truncated thread stack frames for event %s");
            }
        }
        return event;
    }

    private static void truncateStackFramesInStackTrace(@NotNull SentryStackTrace stacktrace, @NotNull SentryEvent event, @NotNull SentryOptions options, @NotNull String logMessage) {
        @Nullable List<SentryStackFrame> frames = stacktrace.getFrames();
        if (frames != null && frames.size() > 500) {
            @NotNull ArrayList<SentryStackFrame> truncatedFrames = new ArrayList<SentryStackFrame>(500);
            truncatedFrames.addAll(frames.subList(0, 250));
            truncatedFrames.addAll(frames.subList(frames.size() - 250, frames.size()));
            stacktrace.setFrames(truncatedFrames);
            options.getLogger().log(SentryLevel.DEBUG, logMessage, event.getEventId());
        }
    }
}

