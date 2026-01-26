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
import io.sentry.logger.SentryLogParameters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILoggerApi {
    public void trace(@Nullable String var1, Object ... var2);

    public void debug(@Nullable String var1, Object ... var2);

    public void info(@Nullable String var1, Object ... var2);

    public void warn(@Nullable String var1, Object ... var2);

    public void error(@Nullable String var1, Object ... var2);

    public void fatal(@Nullable String var1, Object ... var2);

    public void log(@NotNull SentryLogLevel var1, @Nullable String var2, Object ... var3);

    public void log(@NotNull SentryLogLevel var1, @Nullable SentryDate var2, @Nullable String var3, Object ... var4);

    public void log(@NotNull SentryLogLevel var1, @NotNull SentryLogParameters var2, @Nullable String var3, Object ... var4);
}

