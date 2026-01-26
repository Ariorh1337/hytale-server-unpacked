/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.camera.asset.viewbobbing;

import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.builtin.adventure.camera.asset.viewbobbing.ViewBobbing;
import com.hypixel.hytale.protocol.CachedPacket;
import com.hypixel.hytale.protocol.MovementType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateViewBobbing;
import com.hypixel.hytale.server.core.asset.packet.SimpleAssetPacketGenerator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class ViewBobbingPacketGenerator
extends SimpleAssetPacketGenerator<MovementType, ViewBobbing, AssetMap<MovementType, ViewBobbing>> {
    @Override
    @Nonnull
    public Packet generateInitPacket(AssetMap<MovementType, ViewBobbing> assetMap, @Nonnull Map<MovementType, ViewBobbing> assets) {
        return ViewBobbingPacketGenerator.toCachedPacket(UpdateType.Init, assets);
    }

    @Override
    @Nonnull
    protected Packet generateUpdatePacket(AssetMap<MovementType, ViewBobbing> assetMap, @Nonnull Map<MovementType, ViewBobbing> loadedAssets) {
        return ViewBobbingPacketGenerator.toCachedPacket(UpdateType.AddOrUpdate, loadedAssets);
    }

    @Override
    @Nonnull
    protected Packet generateRemovePacket(AssetMap<MovementType, ViewBobbing> assetMap, @Nonnull Set<MovementType> removed) {
        UpdateViewBobbing packet = new UpdateViewBobbing();
        packet.type = UpdateType.Remove;
        packet.profiles = new EnumMap<MovementType, com.hypixel.hytale.protocol.ViewBobbing>(MovementType.class);
        for (MovementType type : removed) {
            packet.profiles.put(type, null);
        }
        return CachedPacket.cache(packet);
    }

    @Nonnull
    protected static Packet toCachedPacket(UpdateType type, @Nonnull Map<MovementType, ViewBobbing> assets) {
        UpdateViewBobbing packet = new UpdateViewBobbing();
        packet.type = type;
        packet.profiles = new EnumMap<MovementType, com.hypixel.hytale.protocol.ViewBobbing>(MovementType.class);
        for (Map.Entry<MovementType, ViewBobbing> entry : assets.entrySet()) {
            packet.profiles.put(entry.getKey(), entry.getValue().toPacket());
        }
        return CachedPacket.cache(packet);
    }
}

