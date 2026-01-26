/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.RequestDetails;
import io.sentry.SentryOptions;
import io.sentry.transport.ITransport;
import org.jetbrains.annotations.NotNull;

public interface ITransportFactory {
    @NotNull
    public ITransport create(@NotNull SentryOptions var1, @NotNull RequestDetails var2);
}

