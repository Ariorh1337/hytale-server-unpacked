/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.worldlocationproviders;

import com.hypixel.hytale.builtin.adventure.objectives.config.worldlocationproviders.CheckTagWorldHeightRadiusProvider;
import com.hypixel.hytale.builtin.adventure.objectives.config.worldlocationproviders.LocationRadiusProvider;
import com.hypixel.hytale.builtin.adventure.objectives.config.worldlocationproviders.LookBlocksBelowProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class WorldLocationProvider {
    public static final CodecMapCodec<WorldLocationProvider> CODEC = new CodecMapCodec("Type");
    public static final BuilderCodec<WorldLocationProvider> BASE_CODEC = BuilderCodec.abstractBuilder(WorldLocationProvider.class).build();

    @Nullable
    public abstract Vector3i runCondition(World var1, Vector3i var2);

    public abstract boolean equals(Object var1);

    public abstract int hashCode();

    @Nonnull
    public String toString() {
        return "WorldLocationProvider{}";
    }

    static {
        CODEC.register("LookBlocksBelow", (Class<WorldLocationProvider>)LookBlocksBelowProvider.class, (Codec<WorldLocationProvider>)LookBlocksBelowProvider.CODEC);
        CODEC.register("LocationRadius", (Class<WorldLocationProvider>)LocationRadiusProvider.class, (Codec<WorldLocationProvider>)LocationRadiusProvider.CODEC);
        CODEC.register("TagBlockHeight", (Class<WorldLocationProvider>)CheckTagWorldHeightRadiusProvider.class, (Codec<WorldLocationProvider>)CheckTagWorldHeightRadiusProvider.CODEC);
    }
}

