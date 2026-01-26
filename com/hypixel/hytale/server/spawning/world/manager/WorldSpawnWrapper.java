/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.world.manager;

import com.hypixel.hytale.server.spawning.assets.spawns.config.WorldNPCSpawn;
import com.hypixel.hytale.server.spawning.wrappers.SpawnWrapper;
import javax.annotation.Nonnull;

public class WorldSpawnWrapper
extends SpawnWrapper<WorldNPCSpawn> {
    public WorldSpawnWrapper(@Nonnull WorldNPCSpawn spawn) {
        super(WorldNPCSpawn.getAssetMap().getIndex(spawn.getId()), spawn);
    }

    public double getMoonPhaseWeightModifier(int moonPhase) {
        double[] moonPhaseWeights = ((WorldNPCSpawn)this.spawn).getMoonPhaseWeightModifiers();
        if (moonPhaseWeights == null) {
            return 1.0;
        }
        if (moonPhase >= moonPhaseWeights.length) {
            return 0.0;
        }
        return moonPhaseWeights[moonPhase];
    }
}

