/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.hints;

import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DiskFlushNotification {
    public void markFlushed();

    public boolean isFlushable(@Nullable SentryId var1);

    public void setFlushable(@NotNull SentryId var1);
}

