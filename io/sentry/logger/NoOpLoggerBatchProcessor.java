/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.logger;

import io.sentry.SentryLogEvent;
import io.sentry.logger.ILoggerBatchProcessor;
import org.jetbrains.annotations.NotNull;

public final class NoOpLoggerBatchProcessor
implements ILoggerBatchProcessor {
    private static final NoOpLoggerBatchProcessor instance = new NoOpLoggerBatchProcessor();

    private NoOpLoggerBatchProcessor() {
    }

    public static NoOpLoggerBatchProcessor getInstance() {
        return instance;
    }

    @Override
    public void add(@NotNull SentryLogEvent event) {
    }

    @Override
    public void close(boolean isRestarting) {
    }

    @Override
    public void flush(long timeoutMillis) {
    }
}

