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

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@ApiStatus.Internal
public final class DiagnosticLogger
implements ILogger {
    @NotNull
    private final SentryOptions options;
    @Nullable
    private final ILogger logger;

    public DiagnosticLogger(@NotNull SentryOptions options, @Nullable ILogger logger) {
        this.options = Objects.requireNonNull(options, "SentryOptions is required.");
        this.logger = logger;
    }

    @Override
    public boolean isEnabled(@Nullable SentryLevel level) {
        SentryLevel diagLevel = this.options.getDiagnosticLevel();
        if (level == null) {
            return false;
        }
        return this.options.isDebug() && level.ordinal() >= diagLevel.ordinal();
    }

    @Override
    public void log(@NotNull SentryLevel level, @NotNull String message, Object ... args) {
        if (this.logger != null && this.isEnabled(level)) {
            this.logger.log(level, message, args);
        }
    }

    @Override
    public void log(@NotNull SentryLevel level, @NotNull String message, @Nullable Throwable throwable) {
        if (this.logger != null && this.isEnabled(level)) {
            this.logger.log(level, message, throwable);
        }
    }

    @Override
    public void log(@NotNull SentryLevel level, @Nullable Throwable throwable, @NotNull String message, Object ... args) {
        if (this.logger != null && this.isEnabled(level)) {
            this.logger.log(level, throwable, message, args);
        }
    }

    @TestOnly
    @Nullable
    public ILogger getLogger() {
        return this.logger;
    }
}

