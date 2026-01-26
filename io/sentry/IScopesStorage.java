/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.IScopes;
import io.sentry.ISentryLifecycleToken;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface IScopesStorage {
    public void init();

    @NotNull
    public ISentryLifecycleToken set(@Nullable IScopes var1);

    @Nullable
    public IScopes get();

    public void close();
}

