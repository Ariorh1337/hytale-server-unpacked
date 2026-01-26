/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.IConnectionStatusProvider;
import java.io.IOException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class NoOpConnectionStatusProvider
implements IConnectionStatusProvider {
    @Override
    @NotNull
    public IConnectionStatusProvider.ConnectionStatus getConnectionStatus() {
        return IConnectionStatusProvider.ConnectionStatus.UNKNOWN;
    }

    @Override
    @Nullable
    public String getConnectionType() {
        return null;
    }

    @Override
    public boolean addConnectionStatusObserver(@NotNull IConnectionStatusProvider.IConnectionStatusObserver observer) {
        return false;
    }

    @Override
    public void removeConnectionStatusObserver(@NotNull IConnectionStatusProvider.IConnectionStatusObserver observer) {
    }

    @Override
    public void close() throws IOException {
    }
}

