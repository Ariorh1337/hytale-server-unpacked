/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.repulsion;

import com.hypixel.hytale.assetstore.AssetUpdateQuery;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateRepulsionConfig;
import com.hypixel.hytale.server.core.asset.packet.AssetPacketGenerator;
import com.hypixel.hytale.server.core.modules.entity.repulsion.RepulsionConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class RepulsionConfigPacketGenerator
extends AssetPacketGenerator<String, RepulsionConfig, IndexedLookupTableAssetMap<String, RepulsionConfig>> {
    @Override
    @Nonnull
    public Packet generateInitPacket(@Nonnull IndexedLookupTableAssetMap<String, RepulsionConfig> assetMap, @Nonnull Map<String, RepulsionConfig> assets) {
        Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.RepulsionConfig> repulsionConfigs = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.RepulsionConfig>();
        for (Map.Entry<String, RepulsionConfig> entry : assets.entrySet()) {
            repulsionConfigs.put(assetMap.getIndex(entry.getKey()), entry.getValue().toPacket());
        }
        return new UpdateRepulsionConfig(UpdateType.Init, assetMap.getNextIndex(), repulsionConfigs);
    }

    @Override
    @Nonnull
    public Packet generateUpdatePacket(@Nonnull IndexedLookupTableAssetMap<String, RepulsionConfig> assetMap, @Nonnull Map<String, RepulsionConfig> loadedAssets, @Nonnull AssetUpdateQuery query) {
        Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.RepulsionConfig> repulsionConfigs = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.RepulsionConfig>();
        for (Map.Entry<String, RepulsionConfig> entry : loadedAssets.entrySet()) {
            repulsionConfigs.put(assetMap.getIndex(entry.getKey()), entry.getValue().toPacket());
        }
        return new UpdateRepulsionConfig(UpdateType.AddOrUpdate, assetMap.getNextIndex(), repulsionConfigs);
    }

    @Override
    @Nonnull
    public Packet generateRemovePacket(@Nonnull IndexedLookupTableAssetMap<String, RepulsionConfig> assetMap, @Nonnull Set<String> removed, @Nonnull AssetUpdateQuery query) {
        Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.RepulsionConfig> repulsionConfigs = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.RepulsionConfig>();
        for (String entry : removed) {
            repulsionConfigs.put(assetMap.getIndex(entry), new com.hypixel.hytale.protocol.RepulsionConfig());
        }
        return new UpdateRepulsionConfig(UpdateType.Remove, assetMap.getNextIndex(), repulsionConfigs);
    }
}

