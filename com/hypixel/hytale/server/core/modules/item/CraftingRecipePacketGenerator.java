/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.item;

import com.hypixel.hytale.assetstore.AssetUpdateQuery;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateRecipes;
import com.hypixel.hytale.server.core.asset.packet.AssetPacketGenerator;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class CraftingRecipePacketGenerator
extends AssetPacketGenerator<String, CraftingRecipe, DefaultAssetMap<String, CraftingRecipe>> {
    @Override
    @Nonnull
    public Packet generateInitPacket(DefaultAssetMap<String, CraftingRecipe> assetMap, @Nonnull Map<String, CraftingRecipe> assets) {
        UpdateRecipes packet = new UpdateRecipes();
        packet.type = UpdateType.Init;
        packet.recipes = new Object2ObjectOpenHashMap<String, com.hypixel.hytale.protocol.CraftingRecipe>();
        for (Map.Entry<String, CraftingRecipe> entry : assets.entrySet()) {
            packet.recipes.put(entry.getKey(), entry.getValue().toPacket(entry.getKey()));
        }
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateUpdatePacket(DefaultAssetMap<String, CraftingRecipe> assetMap, @Nonnull Map<String, CraftingRecipe> loadedAssets, @Nonnull AssetUpdateQuery query) {
        UpdateRecipes packet = new UpdateRecipes();
        packet.type = UpdateType.AddOrUpdate;
        packet.recipes = new Object2ObjectOpenHashMap<String, com.hypixel.hytale.protocol.CraftingRecipe>();
        for (Map.Entry<String, CraftingRecipe> entry : loadedAssets.entrySet()) {
            packet.recipes.put(entry.getKey(), entry.getValue().toPacket(entry.getKey()));
        }
        return packet;
    }

    @Override
    @Nonnull
    public Packet generateRemovePacket(DefaultAssetMap<String, CraftingRecipe> assetMap, @Nonnull Set<String> removed, @Nonnull AssetUpdateQuery query) {
        UpdateRecipes packet = new UpdateRecipes();
        packet.type = UpdateType.Remove;
        packet.removedRecipes = (String[])removed.toArray(String[]::new);
        return packet;
    }
}

