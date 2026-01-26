/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.accesscontrol.provider;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public interface AccessProvider {
    @Nonnull
    public CompletableFuture<Optional<String>> getDisconnectReason(UUID var1);
}

