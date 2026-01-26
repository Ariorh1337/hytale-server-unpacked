/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.ITransportFactory;
import io.sentry.RequestDetails;
import io.sentry.SentryOptions;
import io.sentry.transport.AsyncHttpTransport;
import io.sentry.transport.ITransport;
import io.sentry.transport.RateLimiter;
import io.sentry.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class AsyncHttpTransportFactory
implements ITransportFactory {
    @Override
    @NotNull
    public ITransport create(@NotNull SentryOptions options, @NotNull RequestDetails requestDetails) {
        Objects.requireNonNull(options, "options is required");
        Objects.requireNonNull(requestDetails, "requestDetails is required");
        return new AsyncHttpTransport(options, new RateLimiter(options), options.getTransportGate(), requestDetails);
    }
}

