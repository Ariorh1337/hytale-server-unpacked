/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entityui.asset;

import com.hypixel.hytale.assetstore.AssetUpdateQuery;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateEntityUIComponents;
import com.hypixel.hytale.server.core.asset.packet.AssetPacketGenerator;
import com.hypixel.hytale.server.core.modules.entityui.asset.EntityUIComponent;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class EntityUIComponentPacketGenerator
extends AssetPacketGenerator<String, EntityUIComponent, IndexedLookupTableAssetMap<String, EntityUIComponent>> {
    @Override
    @Nonnull
    public Packet generateInitPacket(@Nonnull IndexedLookupTableAssetMap<String, EntityUIComponent> assetMap, @Nonnull Map<String, EntityUIComponent> assets) {
        Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.EntityUIComponent> configs = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.EntityUIComponent>();
        for (Map.Entry<String, EntityUIComponent> entry : assets.entrySet()) {
            configs.put(assetMap.getIndex(entry.getKey()), entry.getValue().toPacket());
        }
        return new UpdateEntityUIComponents(UpdateType.Init, assetMap.getNextIndex(), configs);
    }

    @Override
    @Nonnull
    public Packet generateUpdatePacket(@Nonnull IndexedLookupTableAssetMap<String, EntityUIComponent> assetMap, @Nonnull Map<String, EntityUIComponent> loadedAssets, @Nonnull AssetUpdateQuery query) {
        Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.EntityUIComponent> components = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.EntityUIComponent>();
        for (Map.Entry<String, EntityUIComponent> entry : loadedAssets.entrySet()) {
            components.put(assetMap.getIndex(entry.getKey()), entry.getValue().toPacket());
        }
        return new UpdateEntityUIComponents(UpdateType.AddOrUpdate, assetMap.getNextIndex(), components);
    }

    @Override
    @Nonnull
    public Packet generateRemovePacket(@Nonnull IndexedLookupTableAssetMap<String, EntityUIComponent> assetMap, @Nonnull Set<String> removed, @Nonnull AssetUpdateQuery query) {
        Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.EntityUIComponent> configs = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.EntityUIComponent>();
        for (String entry : removed) {
            configs.put(assetMap.getIndex(entry), new com.hypixel.hytale.protocol.EntityUIComponent());
        }
        return new UpdateEntityUIComponents(UpdateType.Remove, assetMap.getNextIndex(), configs);
    }
}

