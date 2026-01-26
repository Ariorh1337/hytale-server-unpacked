/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.item;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.protocol.ItemQuality;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateItemQualities;
import com.hypixel.hytale.server.core.asset.packet.SimpleAssetPacketGenerator;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class ItemQualityPacketGenerator
extends SimpleAssetPacketGenerator<String, com.hypixel.hytale.server.core.asset.type.item.config.ItemQuality, IndexedLookupTableAssetMap<String, com.hypixel.hytale.server.core.asset.type.item.config.ItemQuality>> {
    @Override
    @Nonnull
    public Packet generateInitPacket(@Nonnull IndexedLookupTableAssetMap<String, com.hypixel.hytale.server.core.asset.type.item.config.ItemQuality> assetMap, @Nonnull Map<String, com.hypixel.hytale.server.core.asset.type.item.config.ItemQuality> assets) {
        UpdateItemQualities packet = new UpdateItemQualities();
        packet.type = UpdateType.Init;
        packet.itemQualities = new Int2ObjectOpenHashMap<ItemQuality>();
        for (Map.Entry<String, com.hypixel.hytale.server.core.asset.type.item.config.ItemQuality> entry : assets.entrySet()) {
            String key = entry.getKey();
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.itemQualities.put(index, entry.getValue().toPacket());
        }
        packet.maxId = assetMap.getNextIndex();
        return packet;
    }

    @Override
    @Nonnull
    protected Packet generateUpdatePacket(@Nonnull IndexedLookupTableAssetMap<String, com.hypixel.hytale.server.core.asset.type.item.config.ItemQuality> assetMap, @Nonnull Map<String, com.hypixel.hytale.server.core.asset.type.item.config.ItemQuality> loadedAssets) {
        UpdateItemQualities packet = new UpdateItemQualities();
        packet.type = UpdateType.AddOrUpdate;
        packet.itemQualities = new Int2ObjectOpenHashMap<ItemQuality>();
        for (Map.Entry<String, com.hypixel.hytale.server.core.asset.type.item.config.ItemQuality> entry : loadedAssets.entrySet()) {
            String key = entry.getKey();
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.itemQualities.put(index, entry.getValue().toPacket());
        }
        packet.maxId = assetMap.getNextIndex();
        return packet;
    }

    @Override
    @Nonnull
    protected Packet generateRemovePacket(@Nonnull IndexedLookupTableAssetMap<String, com.hypixel.hytale.server.core.asset.type.item.config.ItemQuality> assetMap, @Nonnull Set<String> removed) {
        UpdateItemQualities packet = new UpdateItemQualities();
        packet.type = UpdateType.Remove;
        packet.itemQualities = new Int2ObjectOpenHashMap<ItemQuality>();
        for (String key : removed) {
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.itemQualities.put(index, null);
        }
        packet.maxId = assetMap.getNextIndex();
        return packet;
    }
}

