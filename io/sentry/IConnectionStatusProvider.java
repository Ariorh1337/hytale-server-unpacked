/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import java.io.Closeable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface IConnectionStatusProvider
extends Closeable {
    @NotNull
    public ConnectionStatus getConnectionStatus();

    @Nullable
    public String getConnectionType();

    public boolean addConnectionStatusObserver(@NotNull IConnectionStatusObserver var1);

    public void removeConnectionStatusObserver(@NotNull IConnectionStatusObserver var1);

    public static interface IConnectionStatusObserver {
        public void onConnectionStatusChanged(@NotNull ConnectionStatus var1);
    }

    public static enum ConnectionStatus {
        UNKNOWN,
        CONNECTED,
        DISCONNECTED,
        NO_PERMISSION;

    }
}

