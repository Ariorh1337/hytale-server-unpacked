/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage.resources;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.EmptyResourceStorage;
import com.hypixel.hytale.component.IResourceStorage;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldProvider;
import com.hypixel.hytale.server.core.universe.world.storage.resources.IResourceStorageProvider;
import javax.annotation.Nonnull;

public class EmptyResourceStorageProvider
implements IResourceStorageProvider {
    public static final EmptyResourceStorageProvider INSTANCE = new EmptyResourceStorageProvider();
    public static final String ID = "Empty";
    public static final BuilderCodec<EmptyResourceStorageProvider> CODEC = BuilderCodec.builder(EmptyResourceStorageProvider.class, () -> INSTANCE).build();

    @Override
    @Nonnull
    public <T extends WorldProvider> IResourceStorage getResourceStorage(@Nonnull World world) {
        return EmptyResourceStorage.get();
    }

    @Nonnull
    public String toString() {
        return "EmptyResourceStorageProvider{}";
    }
}

