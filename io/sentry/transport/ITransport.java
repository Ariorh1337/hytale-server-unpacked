/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.transport;

import io.sentry.Hint;
import io.sentry.SentryEnvelope;
import io.sentry.transport.RateLimiter;
import java.io.Closeable;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ITransport
extends Closeable {
    public void send(@NotNull SentryEnvelope var1, @NotNull Hint var2) throws IOException;

    default public void send(@NotNull SentryEnvelope envelope) throws IOException {
        this.send(envelope, new Hint());
    }

    default public boolean isHealthy() {
        return true;
    }

    public void flush(long var1);

    @Nullable
    public RateLimiter getRateLimiter();

    public void close(boolean var1) throws IOException;
}

