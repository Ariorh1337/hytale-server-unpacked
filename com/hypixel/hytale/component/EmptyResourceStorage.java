/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component;

import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.IResourceStorage;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class EmptyResourceStorage
implements IResourceStorage {
    private static final EmptyResourceStorage INSTANCE = new EmptyResourceStorage();

    public static EmptyResourceStorage get() {
        return INSTANCE;
    }

    @Override
    @Nonnull
    public <T extends Resource<ECS_TYPE>, ECS_TYPE> CompletableFuture<T> load(@Nonnull Store<ECS_TYPE> store, @Nonnull ComponentRegistry.Data<ECS_TYPE> data, @Nonnull ResourceType<ECS_TYPE, T> resourceType) {
        return CompletableFuture.completedFuture(data.createResource(resourceType));
    }

    @Override
    @Nonnull
    public <T extends Resource<ECS_TYPE>, ECS_TYPE> CompletableFuture<Void> save(@Nonnull Store<ECS_TYPE> store, @Nonnull ComponentRegistry.Data<ECS_TYPE> data, @Nonnull ResourceType<ECS_TYPE, T> resourceType, T resource) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Nonnull
    public <T extends Resource<ECS_TYPE>, ECS_TYPE> CompletableFuture<Void> remove(@Nonnull Store<ECS_TYPE> store, @Nonnull ComponentRegistry.Data<ECS_TYPE> data, @Nonnull ResourceType<ECS_TYPE, T> resourceType) {
        return CompletableFuture.completedFuture(null);
    }
}

