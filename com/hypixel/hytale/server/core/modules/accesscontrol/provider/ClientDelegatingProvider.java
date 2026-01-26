/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.accesscontrol.provider;

import com.hypixel.hytale.server.core.modules.accesscontrol.provider.AccessProvider;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class ClientDelegatingProvider
implements AccessProvider {
    @Override
    @Nonnull
    public CompletableFuture<Optional<String>> getDisconnectReason(UUID uuid) {
        return CompletableFuture.completedFuture(Optional.empty());
    }
}

