/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.projectile.config;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateProjectileConfigs;
import com.hypixel.hytale.server.core.asset.packet.DefaultAssetPacketGenerator;
import com.hypixel.hytale.server.core.modules.projectile.config.ProjectileConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProjectileConfigPacketGenerator
extends DefaultAssetPacketGenerator<String, ProjectileConfig> {
    @Override
    @Nonnull
    public Packet generateInitPacket(@Nonnull DefaultAssetMap<String, ProjectileConfig> assetMap, Map<String, ProjectileConfig> assets) {
        UpdateProjectileConfigs packet = new UpdateProjectileConfigs();
        packet.type = UpdateType.Init;
        Object2ObjectOpenHashMap<String, com.hypixel.hytale.protocol.ProjectileConfig> map = new Object2ObjectOpenHashMap<String, com.hypixel.hytale.protocol.ProjectileConfig>();
        for (Map.Entry<String, ProjectileConfig> entry : assetMap.getAssetMap().entrySet()) {
            if (map.put(entry.getKey(), entry.getValue().toPacket()) == null) continue;
            throw new IllegalStateException("Duplicate key");
        }
        packet.configs = map;
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateUpdatePacket(@Nonnull Map<String, ProjectileConfig> loadedAssets) {
        UpdateProjectileConfigs packet = new UpdateProjectileConfigs();
        packet.type = UpdateType.AddOrUpdate;
        Object2ObjectOpenHashMap<String, com.hypixel.hytale.protocol.ProjectileConfig> map = new Object2ObjectOpenHashMap<String, com.hypixel.hytale.protocol.ProjectileConfig>();
        for (Map.Entry<String, ProjectileConfig> entry : loadedAssets.entrySet()) {
            if (map.put(entry.getKey(), entry.getValue().toPacket()) == null) continue;
            throw new IllegalStateException("Duplicate key");
        }
        packet.configs = map;
        return packet;
    }

    @Override
    @Nullable
    public Packet generateRemovePacket(@Nonnull Set<String> removed) {
        UpdateProjectileConfigs packet = new UpdateProjectileConfigs();
        packet.type = UpdateType.Remove;
        packet.removedConfigs = (String[])removed.toArray(String[]::new);
        return packet;
    }
}

