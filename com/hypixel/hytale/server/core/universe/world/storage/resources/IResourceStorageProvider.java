/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage.resources;

import com.hypixel.hytale.codec.lookup.BuilderCodecMapCodec;
import com.hypixel.hytale.component.IResourceStorage;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldProvider;
import javax.annotation.Nonnull;

public interface IResourceStorageProvider {
    @Nonnull
    public static final BuilderCodecMapCodec<IResourceStorageProvider> CODEC = new BuilderCodecMapCodec("Type", true);

    public <T extends WorldProvider> IResourceStorage getResourceStorage(@Nonnull World var1);
}

