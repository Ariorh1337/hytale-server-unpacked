/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.SentryLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILogger {
    public void log(@NotNull SentryLevel var1, @NotNull String var2, Object ... var3);

    public void log(@NotNull SentryLevel var1, @NotNull String var2, @Nullable Throwable var3);

    public void log(@NotNull SentryLevel var1, @Nullable Throwable var2, @NotNull String var3, Object ... var4);

    public boolean isEnabled(@Nullable SentryLevel var1);
}

