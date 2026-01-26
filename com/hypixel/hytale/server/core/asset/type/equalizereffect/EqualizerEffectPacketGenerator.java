/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.equalizereffect;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateEqualizerEffects;
import com.hypixel.hytale.server.core.asset.packet.SimpleAssetPacketGenerator;
import com.hypixel.hytale.server.core.asset.type.equalizereffect.config.EqualizerEffect;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class EqualizerEffectPacketGenerator
extends SimpleAssetPacketGenerator<String, EqualizerEffect, IndexedLookupTableAssetMap<String, EqualizerEffect>> {
    @Override
    @Nonnull
    public Packet generateInitPacket(@Nonnull IndexedLookupTableAssetMap<String, EqualizerEffect> assetMap, @Nonnull Map<String, EqualizerEffect> assets) {
        UpdateEqualizerEffects packet = new UpdateEqualizerEffects();
        packet.type = UpdateType.Init;
        packet.effects = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.EqualizerEffect>(assets.size());
        for (Map.Entry<String, EqualizerEffect> entry : assets.entrySet()) {
            String key = entry.getKey();
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.effects.put(index, entry.getValue().toPacket());
        }
        packet.maxId = assetMap.getNextIndex();
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateUpdatePacket(@Nonnull IndexedLookupTableAssetMap<String, EqualizerEffect> assetMap, @Nonnull Map<String, EqualizerEffect> loadedAssets) {
        UpdateEqualizerEffects packet = new UpdateEqualizerEffects();
        packet.type = UpdateType.AddOrUpdate;
        packet.effects = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.EqualizerEffect>(loadedAssets.size());
        for (Map.Entry<String, EqualizerEffect> entry : loadedAssets.entrySet()) {
            String key = entry.getKey();
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.effects.put(index, entry.getValue().toPacket());
        }
        packet.maxId = assetMap.getNextIndex();
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateRemovePacket(@Nonnull IndexedLookupTableAssetMap<String, EqualizerEffect> assetMap, @Nonnull Set<String> removed) {
        UpdateEqualizerEffects packet = new UpdateEqualizerEffects();
        packet.type = UpdateType.Remove;
        packet.effects = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.EqualizerEffect>(removed.size());
        for (String key : removed) {
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.effects.put(index, null);
        }
        packet.maxId = assetMap.getNextIndex();
        return packet;
    }
}

