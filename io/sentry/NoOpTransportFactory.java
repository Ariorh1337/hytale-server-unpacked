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
import io.sentry.transport.ITransport;
import io.sentry.transport.NoOpTransport;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class NoOpTransportFactory
implements ITransportFactory {
    private static final NoOpTransportFactory instance = new NoOpTransportFactory();

    public static NoOpTransportFactory getInstance() {
        return instance;
    }

    private NoOpTransportFactory() {
    }

    @Override
    @NotNull
    public ITransport create(@NotNull SentryOptions options, @NotNull RequestDetails requestDetails) {
        return NoOpTransport.getInstance();
    }
}

