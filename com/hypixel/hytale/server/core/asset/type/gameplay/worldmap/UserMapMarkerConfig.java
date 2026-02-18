/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.gameplay.worldmap;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public final class UserMapMarkerConfig {
    public static final BuilderCodec<UserMapMarkerConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(UserMapMarkerConfig.class, UserMapMarkerConfig::new).append(new KeyedCodec<Boolean>("AllowCreatingMarkers", Codec.BOOLEAN), (config, o) -> {
        config.allowCreatingMarkers = o;
    }, config -> config.allowCreatingMarkers).documentation("Whether players can create new map markers. This will also make the option invisible from the client.").add()).append(new KeyedCodec<Integer>("MaxPersonalMarkersPerPlayer", Codec.INTEGER), (config, o) -> {
        config.maxPersonalMarkersPerPlayer = o;
    }, config -> config.maxPersonalMarkersPerPlayer).add()).append(new KeyedCodec<Integer>("MaxSharedMarkersPerPlayer", Codec.INTEGER), (config, o) -> {
        config.maxSharedMarkersPerPlayer = o;
    }, config -> config.maxSharedMarkersPerPlayer).add()).append(new KeyedCodec<Boolean>("AllowDeletingOtherPlayersSharedMarkers", Codec.BOOLEAN), (config, o) -> {
        config.allowDeleteOtherPlayersSharedMarkers = o;
    }, config -> config.allowDeleteOtherPlayersSharedMarkers).documentation("Whether a player can delete another player's shared marker. Beware of marker pvp.").add()).build();
    private boolean allowCreatingMarkers = true;
    private int maxPersonalMarkersPerPlayer = 12;
    private int maxSharedMarkersPerPlayer = 12;
    private boolean allowDeleteOtherPlayersSharedMarkers = true;

    public boolean isAllowCreatingMarkers() {
        return this.allowCreatingMarkers;
    }

    public int getMaxPersonalMarkersPerPlayer() {
        return this.maxPersonalMarkersPerPlayer;
    }

    public int getMaxSharedMarkersPerPlayer() {
        return this.maxSharedMarkersPerPlayer;
    }

    public boolean isAllowDeleteOtherPlayersSharedMarkers() {
        return this.allowDeleteOtherPlayersSharedMarkers;
    }
}

