/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.TestOnly
 */
package io.sentry;

import io.sentry.SentryStackTraceFactory;
import io.sentry.protocol.SentryStackFrame;
import io.sentry.protocol.SentryStackTrace;
import io.sentry.protocol.SentryThread;
import io.sentry.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@ApiStatus.Internal
public final class SentryThreadFactory {
    @NotNull
    private final SentryStackTraceFactory sentryStackTraceFactory;

    public SentryThreadFactory(@NotNull SentryStackTraceFactory sentryStackTraceFactory) {
        this.sentryStackTraceFactory = Objects.requireNonNull(sentryStackTraceFactory, "The SentryStackTraceFactory is required.");
    }

    @Nullable
    List<SentryThread> getCurrentThread(boolean attachStackTrace) {
        HashMap<Thread, StackTraceElement[]> threads = new HashMap<Thread, StackTraceElement[]>();
        Thread currentThread = Thread.currentThread();
        threads.put(currentThread, currentThread.getStackTrace());
        return this.getCurrentThreads(threads, null, false, attachStackTrace);
    }

    @Nullable
    List<SentryThread> getCurrentThreads(@Nullable List<Long> mechanismThreadIds, boolean ignoreCurrentThread, boolean attachStackTrace) {
        return this.getCurrentThreads(Thread.getAllStackTraces(), mechanismThreadIds, ignoreCurrentThread, attachStackTrace);
    }

    @Nullable
    List<SentryThread> getCurrentThreads(@Nullable List<Long> mechanismThreadIds, boolean attachStackTrace) {
        return this.getCurrentThreads(Thread.getAllStackTraces(), mechanismThreadIds, false, attachStackTrace);
    }

    @TestOnly
    @Nullable
    List<SentryThread> getCurrentThreads(@NotNull Map<Thread, StackTraceElement[]> threads, @Nullable List<Long> mechanismThreadIds, boolean ignoreCurrentThread, boolean attachStackTrace) {
        ArrayList<SentryThread> result = null;
        Thread currentThread = Thread.currentThread();
        if (!threads.isEmpty()) {
            result = new ArrayList<SentryThread>();
            if (!threads.containsKey(currentThread)) {
                threads.put(currentThread, currentThread.getStackTrace());
            }
            for (Map.Entry<Thread, StackTraceElement[]> item : threads.entrySet()) {
                Thread thread = item.getKey();
                boolean crashed = thread == currentThread && !ignoreCurrentThread || mechanismThreadIds != null && mechanismThreadIds.contains(thread.getId()) && !ignoreCurrentThread;
                result.add(this.getSentryThread(crashed, item.getValue(), item.getKey(), attachStackTrace));
            }
        }
        return result;
    }

    @NotNull
    private SentryThread getSentryThread(boolean crashed, @NotNull StackTraceElement[] stackFramesElements, @NotNull Thread thread, boolean attachStacktrace) {
        List<SentryStackFrame> frames;
        SentryThread sentryThread = new SentryThread();
        sentryThread.setName(thread.getName());
        sentryThread.setPriority(thread.getPriority());
        sentryThread.setId(thread.getId());
        sentryThread.setDaemon(thread.isDaemon());
        sentryThread.setState(thread.getState().name());
        sentryThread.setCrashed(crashed);
        if (attachStacktrace && (frames = this.sentryStackTraceFactory.getStackFrames(stackFramesElements, false)) != null && !frames.isEmpty()) {
            SentryStackTrace sentryStackTrace = new SentryStackTrace(frames);
            sentryStackTrace.setSnapshot(true);
            sentryThread.setStacktrace(sentryStackTrace);
        }
        return sentryThread;
    }
}

