/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.worldlocationcondition;

import com.hypixel.hytale.builtin.adventure.worldlocationcondition.NeighbourBlockTagsLocationCondition;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.worldlocationcondition.WorldLocationCondition;
import javax.annotation.Nonnull;

public class WorldLocationConditionPlugin
extends JavaPlugin {
    public WorldLocationConditionPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        WorldLocationCondition.CODEC.register("NeighbourBlockTags", (Class<WorldLocationCondition>)NeighbourBlockTagsLocationCondition.class, (Codec<WorldLocationCondition>)NeighbourBlockTagsLocationCondition.CODEC);
    }
}

