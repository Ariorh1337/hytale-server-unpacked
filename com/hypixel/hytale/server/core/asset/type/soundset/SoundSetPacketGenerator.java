/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.soundset;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateSoundSets;
import com.hypixel.hytale.server.core.asset.packet.SimpleAssetPacketGenerator;
import com.hypixel.hytale.server.core.asset.type.soundset.config.SoundSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class SoundSetPacketGenerator
extends SimpleAssetPacketGenerator<String, SoundSet, IndexedLookupTableAssetMap<String, SoundSet>> {
    @Override
    @Nonnull
    public Packet generateInitPacket(@Nonnull IndexedLookupTableAssetMap<String, SoundSet> assetMap, @Nonnull Map<String, SoundSet> assets) {
        UpdateSoundSets packet = new UpdateSoundSets();
        packet.type = UpdateType.Init;
        packet.soundSets = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.SoundSet>(assets.size());
        for (Map.Entry<String, SoundSet> entry : assets.entrySet()) {
            String key = entry.getKey();
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.soundSets.put(index, entry.getValue().toPacket());
        }
        packet.maxId = assetMap.getNextIndex();
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateUpdatePacket(@Nonnull IndexedLookupTableAssetMap<String, SoundSet> assetMap, @Nonnull Map<String, SoundSet> loadedAssets) {
        UpdateSoundSets packet = new UpdateSoundSets();
        packet.type = UpdateType.AddOrUpdate;
        packet.soundSets = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.SoundSet>(loadedAssets.size());
        for (Map.Entry<String, SoundSet> entry : loadedAssets.entrySet()) {
            String key = entry.getKey();
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.soundSets.put(index, entry.getValue().toPacket());
        }
        packet.maxId = assetMap.getNextIndex();
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateRemovePacket(@Nonnull IndexedLookupTableAssetMap<String, SoundSet> assetMap, @Nonnull Set<String> removed) {
        UpdateSoundSets packet = new UpdateSoundSets();
        packet.type = UpdateType.Remove;
        packet.soundSets = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.SoundSet>(removed.size());
        for (String key : removed) {
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.soundSets.put(index, null);
        }
        packet.maxId = assetMap.getNextIndex();
        return packet;
    }
}

