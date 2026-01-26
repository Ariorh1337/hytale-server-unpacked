/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.SentryOptions;
import io.sentry.protocol.SentryStackFrame;
import io.sentry.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class SentryStackTraceFactory {
    private static final int STACKTRACE_FRAME_LIMIT = 100;
    @NotNull
    private final SentryOptions options;

    public SentryStackTraceFactory(@NotNull SentryOptions options) {
        this.options = options;
    }

    @Nullable
    public List<SentryStackFrame> getStackFrames(@Nullable StackTraceElement[] elements, boolean includeSentryFrames) {
        ArrayList<SentryStackFrame> sentryStackFrames = null;
        if (elements != null && elements.length > 0) {
            sentryStackFrames = new ArrayList<SentryStackFrame>();
            for (StackTraceElement item : elements) {
                if (item == null) continue;
                String className = item.getClassName();
                if (!includeSentryFrames && className.startsWith("io.sentry.") && !className.startsWith("io.sentry.samples.") && !className.startsWith("io.sentry.mobile.")) continue;
                SentryStackFrame sentryStackFrame = new SentryStackFrame();
                sentryStackFrame.setInApp(this.isInApp(className));
                sentryStackFrame.setModule(className);
                sentryStackFrame.setFunction(item.getMethodName());
                sentryStackFrame.setFilename(item.getFileName());
                if (item.getLineNumber() >= 0) {
                    sentryStackFrame.setLineno(item.getLineNumber());
                }
                sentryStackFrame.setNative(item.isNativeMethod());
                sentryStackFrames.add(sentryStackFrame);
                if (sentryStackFrames.size() >= 100) break;
            }
            Collections.reverse(sentryStackFrames);
        }
        return sentryStackFrames;
    }

    @Nullable
    public Boolean isInApp(@Nullable String className) {
        if (className == null || className.isEmpty()) {
            return true;
        }
        List<String> inAppIncludes = this.options.getInAppIncludes();
        for (String include : inAppIncludes) {
            if (!className.startsWith(include)) continue;
            return true;
        }
        List<String> inAppExcludes = this.options.getInAppExcludes();
        for (String exclude : inAppExcludes) {
            if (!className.startsWith(exclude)) continue;
            return false;
        }
        return null;
    }

    @NotNull
    List<SentryStackFrame> getInAppCallStack(@NotNull Throwable exception) {
        StackTraceElement[] stacktrace = exception.getStackTrace();
        List<SentryStackFrame> frames = this.getStackFrames(stacktrace, false);
        if (frames == null) {
            return Collections.emptyList();
        }
        List<SentryStackFrame> inAppFrames = CollectionUtils.filterListEntries(frames, frame -> Boolean.TRUE.equals(frame.isInApp()));
        if (!inAppFrames.isEmpty()) {
            return inAppFrames;
        }
        return CollectionUtils.filterListEntries(frames, frame -> {
            String module = frame.getModule();
            boolean isSystemFrame = false;
            if (module != null) {
                isSystemFrame = module.startsWith("sun.") || module.startsWith("java.") || module.startsWith("android.") || module.startsWith("com.android.");
            }
            return !isSystemFrame;
        });
    }

    @ApiStatus.Internal
    @NotNull
    public List<SentryStackFrame> getInAppCallStack() {
        return this.getInAppCallStack(new Exception());
    }
}

