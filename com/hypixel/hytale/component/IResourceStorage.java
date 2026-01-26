/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component;

import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public interface IResourceStorage {
    @Nonnull
    public <T extends Resource<ECS_TYPE>, ECS_TYPE> CompletableFuture<T> load(@Nonnull Store<ECS_TYPE> var1, @Nonnull ComponentRegistry.Data<ECS_TYPE> var2, @Nonnull ResourceType<ECS_TYPE, T> var3);

    @Nonnull
    public <T extends Resource<ECS_TYPE>, ECS_TYPE> CompletableFuture<Void> save(@Nonnull Store<ECS_TYPE> var1, @Nonnull ComponentRegistry.Data<ECS_TYPE> var2, @Nonnull ResourceType<ECS_TYPE, T> var3, T var4);

    @Nonnull
    public <T extends Resource<ECS_TYPE>, ECS_TYPE> CompletableFuture<Void> remove(@Nonnull Store<ECS_TYPE> var1, @Nonnull ComponentRegistry.Data<ECS_TYPE> var2, @Nonnull ResourceType<ECS_TYPE, T> var3);
}

