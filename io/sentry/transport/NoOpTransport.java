/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.transport;

import io.sentry.Hint;
import io.sentry.SentryEnvelope;
import io.sentry.transport.ITransport;
import io.sentry.transport.RateLimiter;
import java.io.IOException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class NoOpTransport
implements ITransport {
    private static final NoOpTransport instance = new NoOpTransport();

    @NotNull
    public static NoOpTransport getInstance() {
        return instance;
    }

    private NoOpTransport() {
    }

    @Override
    public void send(@NotNull SentryEnvelope envelope, @NotNull Hint hint) throws IOException {
    }

    @Override
    public void flush(long timeoutMillis) {
    }

    @Override
    @Nullable
    public RateLimiter getRateLimiter() {
        return null;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void close(boolean isRestarting) throws IOException {
    }
}

