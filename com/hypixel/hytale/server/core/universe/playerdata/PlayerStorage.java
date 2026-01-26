/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.playerdata;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public interface PlayerStorage {
    @Nonnull
    public CompletableFuture<Holder<EntityStore>> load(@Nonnull UUID var1);

    @Nonnull
    public CompletableFuture<Void> save(@Nonnull UUID var1, @Nonnull Holder<EntityStore> var2);

    @Nonnull
    public CompletableFuture<Void> remove(@Nonnull UUID var1);

    @Nonnull
    public Set<UUID> getPlayers() throws IOException;
}

