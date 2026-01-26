/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpLogger
implements ILogger {
    private static final NoOpLogger instance = new NoOpLogger();

    public static NoOpLogger getInstance() {
        return instance;
    }

    private NoOpLogger() {
    }

    @Override
    public void log(@NotNull SentryLevel level, @NotNull String message, Object ... args) {
    }

    @Override
    public void log(@NotNull SentryLevel level, @NotNull String message, @Nullable Throwable throwable) {
    }

    @Override
    public void log(@NotNull SentryLevel level, @Nullable Throwable throwable, @NotNull String message, Object ... args) {
    }

    @Override
    public boolean isEnabled(@Nullable SentryLevel level) {
        return false;
    }
}

