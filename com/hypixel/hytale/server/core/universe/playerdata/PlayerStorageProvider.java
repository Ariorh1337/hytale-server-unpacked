/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.playerdata;

import com.hypixel.hytale.codec.lookup.BuilderCodecMapCodec;
import com.hypixel.hytale.server.core.universe.playerdata.PlayerStorage;

public interface PlayerStorageProvider {
    public static final BuilderCodecMapCodec<PlayerStorageProvider> CODEC = new BuilderCodecMapCodec("Type", true);

    public PlayerStorage getPlayerStorage();
}

