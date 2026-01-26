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
import io.sentry.logger.ILoggerBatchProcessorFactory;
import io.sentry.logger.LoggerBatchProcessor;
import org.jetbrains.annotations.NotNull;

public final class DefaultLoggerBatchProcessorFactory
implements ILoggerBatchProcessorFactory {
    @Override
    @NotNull
    public ILoggerBatchProcessor create(@NotNull SentryOptions options, @NotNull SentryClient client) {
        return new LoggerBatchProcessor(options, client);
    }
}

