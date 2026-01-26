/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package io.sentry;

import io.sentry.SentryDate;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SentryDateProvider {
    public SentryDate now();
}

