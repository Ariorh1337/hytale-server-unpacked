/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.environment;

import com.hypixel.hytale.assetstore.AssetUpdateQuery;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.WorldEnvironment;
import com.hypixel.hytale.protocol.packets.assets.UpdateEnvironments;
import com.hypixel.hytale.server.core.asset.packet.AssetPacketGenerator;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class EnvironmentPacketGenerator
extends AssetPacketGenerator<String, Environment, IndexedLookupTableAssetMap<String, Environment>> {
    @Override
    @Nonnull
    public Packet generateInitPacket(@Nonnull IndexedLookupTableAssetMap<String, Environment> assetMap, @Nonnull Map<String, Environment> assets) {
        Map assetsFromMap = assetMap.getAssetMap();
        if (assets.size() != assetsFromMap.size()) {
            throw new UnsupportedOperationException("Environments can not handle partial init packets!!!");
        }
        UpdateEnvironments packet = new UpdateEnvironments();
        packet.type = UpdateType.Init;
        packet.environments = new Int2ObjectOpenHashMap<WorldEnvironment>();
        for (Map.Entry<String, Environment> entry : assets.entrySet()) {
            String key = entry.getKey();
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.environments.put(index, entry.getValue().toPacket());
        }
        packet.maxId = assetMap.getNextIndex();
        packet.rebuildMapGeometry = true;
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateUpdatePacket(@Nonnull IndexedLookupTableAssetMap<String, Environment> assetMap, @Nonnull Map<String, Environment> loadedAssets, @Nonnull AssetUpdateQuery query) {
        UpdateEnvironments packet = new UpdateEnvironments();
        packet.type = UpdateType.AddOrUpdate;
        packet.environments = new Int2ObjectOpenHashMap<WorldEnvironment>();
        for (Map.Entry<String, Environment> entry : loadedAssets.entrySet()) {
            String key = entry.getKey();
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.environments.put(index, entry.getValue().toPacket());
        }
        packet.maxId = assetMap.getNextIndex();
        packet.rebuildMapGeometry = query.getRebuildCache().isMapGeometry();
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateRemovePacket(@Nonnull IndexedLookupTableAssetMap<String, Environment> assetMap, @Nonnull Set<String> removed, @Nonnull AssetUpdateQuery query) {
        UpdateEnvironments packet = new UpdateEnvironments();
        packet.type = UpdateType.Remove;
        packet.environments = new Int2ObjectOpenHashMap<WorldEnvironment>();
        for (String key : removed) {
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.environments.put(index, null);
        }
        packet.rebuildMapGeometry = query.getRebuildCache().isMapGeometry();
        return packet;
    }
}

