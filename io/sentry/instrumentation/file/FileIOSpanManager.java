/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.instrumentation.file;

import io.sentry.IScopes;
import io.sentry.ISpan;
import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.SentryOptions;
import io.sentry.SentryStackTraceFactory;
import io.sentry.SpanStatus;
import io.sentry.util.Platform;
import io.sentry.util.StringUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FileIOSpanManager {
    @Nullable
    private final ISpan currentSpan;
    @Nullable
    private final File file;
    @NotNull
    private final SentryOptions options;
    @NotNull
    private SpanStatus spanStatus = SpanStatus.OK;
    private long byteCount;
    @NotNull
    private final SentryStackTraceFactory stackTraceFactory;

    @Nullable
    static ISpan startSpan(@NotNull IScopes scopes, @NotNull String op) {
        ISpan parent = Platform.isAndroid() ? scopes.getTransaction() : scopes.getSpan();
        return parent != null ? parent.startChild(op) : null;
    }

    FileIOSpanManager(@Nullable ISpan currentSpan, @Nullable File file, @NotNull SentryOptions options) {
        this.currentSpan = currentSpan;
        this.file = file;
        this.options = options;
        this.stackTraceFactory = new SentryStackTraceFactory(options);
        SentryIntegrationPackageStorage.getInstance().addIntegration("FileIO");
    }

    <T> T performIO(@NotNull FileIOCallable<T> operation) throws IOException {
        try {
            long resUnboxed;
            T result = operation.call();
            if (result instanceof Integer) {
                int resUnboxed2 = (Integer)result;
                if (resUnboxed2 != -1) {
                    this.byteCount += (long)resUnboxed2;
                }
            } else if (result instanceof Long && (resUnboxed = ((Long)result).longValue()) != -1L) {
                this.byteCount += resUnboxed;
            }
            return result;
        }
        catch (IOException exception) {
            this.spanStatus = SpanStatus.INTERNAL_ERROR;
            if (this.currentSpan != null) {
                this.currentSpan.setThrowable(exception);
            }
            throw exception;
        }
    }

    void finish(@NotNull Closeable delegate) throws IOException {
        try {
            delegate.close();
        }
        catch (IOException exception) {
            this.spanStatus = SpanStatus.INTERNAL_ERROR;
            if (this.currentSpan != null) {
                this.currentSpan.setThrowable(exception);
            }
            throw exception;
        }
        finally {
            this.finishSpan();
        }
    }

    private void finishSpan() {
        if (this.currentSpan != null) {
            String byteCountToString = StringUtils.byteCountToString(this.byteCount);
            if (this.file != null) {
                String description = this.getDescription(this.file);
                this.currentSpan.setDescription(description);
                if (this.options.isSendDefaultPii()) {
                    this.currentSpan.setData("file.path", this.file.getAbsolutePath());
                }
            } else {
                this.currentSpan.setDescription(byteCountToString);
            }
            this.currentSpan.setData("file.size", this.byteCount);
            boolean isMainThread = this.options.getThreadChecker().isMainThread();
            this.currentSpan.setData("blocked_main_thread", isMainThread);
            if (isMainThread) {
                this.currentSpan.setData("call_stack", this.stackTraceFactory.getInAppCallStack());
            }
            this.currentSpan.finish(this.spanStatus);
        }
    }

    @NotNull
    private String getDescription(@NotNull File file) {
        String byteCountToString = StringUtils.byteCountToString(this.byteCount);
        if (this.options.isSendDefaultPii()) {
            return file.getName() + " (" + byteCountToString + ")";
        }
        int lastDotIndex = file.getName().lastIndexOf(46);
        if (lastDotIndex > 0 && lastDotIndex < file.getName().length() - 1) {
            String fileExtension = file.getName().substring(lastDotIndex);
            return "***" + fileExtension + " (" + byteCountToString + ")";
        }
        return "*** (" + byteCountToString + ")";
    }

    @FunctionalInterface
    static interface FileIOCallable<T> {
        public T call() throws IOException;
    }
}

