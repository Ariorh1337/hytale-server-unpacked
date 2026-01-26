/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.cache;

import io.sentry.Hint;
import io.sentry.SentryEnvelope;
import org.jetbrains.annotations.NotNull;

public interface IEnvelopeCache
extends Iterable<SentryEnvelope> {
    @Deprecated
    public void store(@NotNull SentryEnvelope var1, @NotNull Hint var2);

    default public boolean storeEnvelope(@NotNull SentryEnvelope envelope, @NotNull Hint hint) {
        this.store(envelope, hint);
        return true;
    }

    @Deprecated
    default public void store(@NotNull SentryEnvelope envelope) {
        this.storeEnvelope(envelope, new Hint());
    }

    public void discard(@NotNull SentryEnvelope var1);
}

