/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.soundevent;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateSoundEvents;
import com.hypixel.hytale.server.core.asset.packet.SimpleAssetPacketGenerator;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class SoundEventPacketGenerator
extends SimpleAssetPacketGenerator<String, SoundEvent, IndexedLookupTableAssetMap<String, SoundEvent>> {
    @Override
    @Nonnull
    public Packet generateInitPacket(@Nonnull IndexedLookupTableAssetMap<String, SoundEvent> assetMap, @Nonnull Map<String, SoundEvent> assets) {
        UpdateSoundEvents packet = new UpdateSoundEvents();
        packet.type = UpdateType.Init;
        packet.soundEvents = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.SoundEvent>(assets.size());
        for (Map.Entry<String, SoundEvent> entry : assets.entrySet()) {
            String key = entry.getKey();
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.soundEvents.put(index, entry.getValue().toPacket());
        }
        packet.maxId = assetMap.getNextIndex();
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateUpdatePacket(@Nonnull IndexedLookupTableAssetMap<String, SoundEvent> assetMap, @Nonnull Map<String, SoundEvent> loadedAssets) {
        UpdateSoundEvents packet = new UpdateSoundEvents();
        packet.type = UpdateType.AddOrUpdate;
        packet.soundEvents = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.SoundEvent>(loadedAssets.size());
        for (Map.Entry<String, SoundEvent> entry : loadedAssets.entrySet()) {
            String key = entry.getKey();
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.soundEvents.put(index, entry.getValue().toPacket());
        }
        packet.maxId = assetMap.getNextIndex();
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateRemovePacket(@Nonnull IndexedLookupTableAssetMap<String, SoundEvent> assetMap, @Nonnull Set<String> removed) {
        UpdateSoundEvents packet = new UpdateSoundEvents();
        packet.type = UpdateType.Remove;
        packet.soundEvents = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.SoundEvent>(removed.size());
        for (String key : removed) {
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            packet.soundEvents.put(index, null);
        }
        packet.maxId = assetMap.getNextIndex();
        return packet;
    }
}

