/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage.resources;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.IResourceStorage;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldProvider;
import com.hypixel.hytale.server.core.universe.world.storage.resources.DiskResourceStorageProvider;
import com.hypixel.hytale.server.core.universe.world.storage.resources.IResourceStorageProvider;
import javax.annotation.Nonnull;

public class DefaultResourceStorageProvider
implements IResourceStorageProvider {
    public static final DefaultResourceStorageProvider INSTANCE = new DefaultResourceStorageProvider();
    public static final String ID = "Hytale";
    public static final BuilderCodec<DefaultResourceStorageProvider> CODEC = BuilderCodec.builder(DefaultResourceStorageProvider.class, () -> INSTANCE).build();
    public static final DiskResourceStorageProvider DEFAULT = new DiskResourceStorageProvider();

    @Override
    @Nonnull
    public <T extends WorldProvider> IResourceStorage getResourceStorage(@Nonnull World world) {
        return DEFAULT.getResourceStorage(world);
    }

    @Nonnull
    public String toString() {
        return "DefaultResourceStorageProvider{}";
    }
}

