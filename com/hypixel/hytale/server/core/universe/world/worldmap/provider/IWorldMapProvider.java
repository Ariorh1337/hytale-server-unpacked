/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.provider;

import com.hypixel.hytale.codec.lookup.BuilderCodecMapCodec;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldmap.IWorldMap;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapLoadException;

public interface IWorldMapProvider {
    public static final BuilderCodecMapCodec<IWorldMapProvider> CODEC = new BuilderCodecMapCodec("Type", true);

    public IWorldMap getGenerator(World var1) throws WorldMapLoadException;
}

