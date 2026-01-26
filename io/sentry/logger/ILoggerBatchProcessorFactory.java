/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.logger;

import io.sentry.SentryClient;
import io.sentry.SentryOptions;
import io.sentry.logger.ILoggerBatchProcessor;
import org.jetbrains.annotations.NotNull;

public interface ILoggerBatchProcessorFactory {
    @NotNull
    public ILoggerBatchProcessor create(@NotNull SentryOptions var1, @NotNull SentryClient var2);
}

