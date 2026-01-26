/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.logger;

import io.sentry.SentryLogEvent;
import org.jetbrains.annotations.NotNull;

public interface ILoggerBatchProcessor {
    public void add(@NotNull SentryLogEvent var1);

    public void close(boolean var1);

    public void flush(long var1);
}

