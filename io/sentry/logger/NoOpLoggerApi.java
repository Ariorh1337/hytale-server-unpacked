/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.logger;

import io.sentry.SentryDate;
import io.sentry.SentryLogLevel;
import io.sentry.logger.ILoggerApi;
import io.sentry.logger.SentryLogParameters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpLoggerApi
implements ILoggerApi {
    private static final NoOpLoggerApi instance = new NoOpLoggerApi();

    private NoOpLoggerApi() {
    }

    public static NoOpLoggerApi getInstance() {
        return instance;
    }

    @Override
    public void trace(@Nullable String message, Object ... args) {
    }

    @Override
    public void debug(@Nullable String message, Object ... args) {
    }

    @Override
    public void info(@Nullable String message, Object ... args) {
    }

    @Override
    public void warn(@Nullable String message, Object ... args) {
    }

    @Override
    public void error(@Nullable String message, Object ... args) {
    }

    @Override
    public void fatal(@Nullable String message, Object ... args) {
    }

    @Override
    public void log(@NotNull SentryLogLevel level, @Nullable String message, Object ... args) {
    }

    @Override
    public void log(@NotNull SentryLogLevel level, @Nullable SentryDate timestamp, @Nullable String message, Object ... args) {
    }

    @Override
    public void log(@NotNull SentryLogLevel level, @NotNull SentryLogParameters params, @Nullable String message, Object ... args) {
    }
}

