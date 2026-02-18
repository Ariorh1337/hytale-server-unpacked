/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.packet;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.server.core.asset.packet.SimpleAssetPacketGenerator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class DefaultAssetPacketGenerator<K, T extends JsonAssetWithMap<K, DefaultAssetMap<K, T>>>
extends SimpleAssetPacketGenerator<K, T, DefaultAssetMap<K, T>> {
    @Override
    public abstract ToClientPacket generateInitPacket(DefaultAssetMap<K, T> var1, Map<K, T> var2);

    public abstract ToClientPacket generateUpdatePacket(Map<K, T> var1);

    @Nullable
    public abstract ToClientPacket generateRemovePacket(Set<K> var1);

    @Override
    public final ToClientPacket generateUpdatePacket(DefaultAssetMap<K, T> assetMap, Map<K, T> loadedAssets) {
        return this.generateUpdatePacket(loadedAssets);
    }

    @Override
    @Nullable
    public final ToClientPacket generateRemovePacket(DefaultAssetMap<K, T> assetMap, Set<K> removed) {
        return this.generateRemovePacket(removed);
    }
}

