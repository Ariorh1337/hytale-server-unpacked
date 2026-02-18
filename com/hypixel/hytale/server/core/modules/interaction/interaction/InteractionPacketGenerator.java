/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction;

import com.hypixel.hytale.assetstore.AssetUpdateQuery;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.protocol.SimpleInteraction;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateInteractions;
import com.hypixel.hytale.server.core.asset.packet.AssetPacketGenerator;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class InteractionPacketGenerator
extends AssetPacketGenerator<String, Interaction, IndexedLookupTableAssetMap<String, Interaction>> {
    @Override
    @Nonnull
    public ToClientPacket generateInitPacket(@Nonnull IndexedLookupTableAssetMap<String, Interaction> assetMap, @Nonnull Map<String, Interaction> assets) {
        Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.Interaction> interactions = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.Interaction>();
        for (Map.Entry<String, Interaction> entry : assets.entrySet()) {
            interactions.put(assetMap.getIndex(entry.getKey()), entry.getValue().toPacket());
        }
        return new UpdateInteractions(UpdateType.Init, assetMap.getNextIndex(), interactions);
    }

    @Override
    @Nonnull
    public ToClientPacket generateUpdatePacket(@Nonnull IndexedLookupTableAssetMap<String, Interaction> assetMap, @Nonnull Map<String, Interaction> loadedAssets, @Nonnull AssetUpdateQuery query) {
        Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.Interaction> interactions = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.Interaction>();
        for (Map.Entry<String, Interaction> entry : loadedAssets.entrySet()) {
            interactions.put(assetMap.getIndex(entry.getKey()), entry.getValue().toPacket());
        }
        return new UpdateInteractions(UpdateType.AddOrUpdate, assetMap.getNextIndex(), interactions);
    }

    @Override
    @Nonnull
    public ToClientPacket generateRemovePacket(@Nonnull IndexedLookupTableAssetMap<String, Interaction> assetMap, @Nonnull Set<String> removed, @Nonnull AssetUpdateQuery query) {
        Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.Interaction> interactions = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.Interaction>();
        for (String entry : removed) {
            interactions.put(assetMap.getIndex(entry), (com.hypixel.hytale.protocol.Interaction)new SimpleInteraction());
        }
        return new UpdateInteractions(UpdateType.Remove, assetMap.getNextIndex(), interactions);
    }
}

