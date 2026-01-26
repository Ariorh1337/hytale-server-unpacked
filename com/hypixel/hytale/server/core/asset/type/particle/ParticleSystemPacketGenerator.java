/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.particle;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateParticleSystems;
import com.hypixel.hytale.server.core.asset.packet.DefaultAssetPacketGenerator;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class ParticleSystemPacketGenerator
extends DefaultAssetPacketGenerator<String, ParticleSystem> {
    @Override
    @Nonnull
    public Packet generateInitPacket(DefaultAssetMap<String, ParticleSystem> assetMap, @Nonnull Map<String, ParticleSystem> assets) {
        UpdateParticleSystems packet = new UpdateParticleSystems();
        packet.type = UpdateType.Init;
        packet.particleSystems = new Object2ObjectOpenHashMap<String, com.hypixel.hytale.protocol.ParticleSystem>();
        for (Map.Entry<String, ParticleSystem> entry : assets.entrySet()) {
            packet.particleSystems.put(entry.getKey(), entry.getValue().toPacket());
        }
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateUpdatePacket(@Nonnull Map<String, ParticleSystem> loadedAssets) {
        UpdateParticleSystems packet = new UpdateParticleSystems();
        packet.type = UpdateType.AddOrUpdate;
        packet.particleSystems = new Object2ObjectOpenHashMap<String, com.hypixel.hytale.protocol.ParticleSystem>();
        for (Map.Entry<String, ParticleSystem> entry : loadedAssets.entrySet()) {
            packet.particleSystems.put(entry.getKey(), entry.getValue().toPacket());
        }
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateRemovePacket(@Nonnull Set<String> removed) {
        UpdateParticleSystems packet = new UpdateParticleSystems();
        packet.type = UpdateType.Remove;
        packet.removedParticleSystems = (String[])removed.toArray(String[]::new);
        return packet;
    }
}

