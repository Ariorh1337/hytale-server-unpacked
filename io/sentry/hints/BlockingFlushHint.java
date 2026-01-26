/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.hints;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.hints.DiskFlushNotification;
import io.sentry.hints.Flushable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public abstract class BlockingFlushHint
implements DiskFlushNotification,
Flushable {
    private final CountDownLatch latch;
    private final long flushTimeoutMillis;
    @NotNull
    private final ILogger logger;

    public BlockingFlushHint(long flushTimeoutMillis, @NotNull ILogger logger) {
        this.flushTimeoutMillis = flushTimeoutMillis;
        this.latch = new CountDownLatch(1);
        this.logger = logger;
    }

    @Override
    public boolean waitFlush() {
        try {
            return this.latch.await(this.flushTimeoutMillis, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.logger.log(SentryLevel.ERROR, "Exception while awaiting for flush in BlockingFlushHint", e);
            return false;
        }
    }

    @Override
    public void markFlushed() {
        this.latch.countDown();
    }
}

