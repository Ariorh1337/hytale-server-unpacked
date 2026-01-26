/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.util.thread;

import io.sentry.protocol.SentryThread;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface IThreadChecker {
    public boolean isMainThread(long var1);

    public boolean isMainThread(@NotNull Thread var1);

    public boolean isMainThread();

    public boolean isMainThread(@NotNull SentryThread var1);

    @NotNull
    public String getCurrentThreadName();

    public long currentThreadSystemId();
}

