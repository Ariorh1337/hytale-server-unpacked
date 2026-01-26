/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.exception;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class SentryEnvelopeException
extends Exception {
    private static final long serialVersionUID = -8307801916948173232L;

    public SentryEnvelopeException(@Nullable String message) {
        super(message);
    }

    public SentryEnvelopeException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

