/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.randomtick;

import com.hypixel.hytale.builtin.randomtick.RandomTick;
import com.hypixel.hytale.builtin.randomtick.RandomTickSystem;
import com.hypixel.hytale.builtin.randomtick.procedures.ChangeIntoBlockProcedure;
import com.hypixel.hytale.builtin.randomtick.procedures.SpreadToProcedure;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.asset.type.blocktick.config.RandomTickProcedure;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;

public class RandomTickPlugin
extends JavaPlugin {
    private static RandomTickPlugin INSTANCE;
    private ResourceType<ChunkStore, RandomTick> randomTickResourceType;

    public static RandomTickPlugin get() {
        return INSTANCE;
    }

    public RandomTickPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
    }

    @Override
    protected void setup() {
        this.randomTickResourceType = this.getChunkStoreRegistry().registerResource(RandomTick.class, RandomTick::new);
        this.getChunkStoreRegistry().registerSystem(new RandomTickSystem());
        RandomTickProcedure.CODEC.register("ChangeIntoBlock", (Class<RandomTickProcedure>)ChangeIntoBlockProcedure.class, (Codec<RandomTickProcedure>)ChangeIntoBlockProcedure.CODEC);
        RandomTickProcedure.CODEC.register("SpreadTo", (Class<RandomTickProcedure>)SpreadToProcedure.class, (Codec<RandomTickProcedure>)SpreadToProcedure.CODEC);
    }

    public ResourceType<ChunkStore, RandomTick> getRandomTickResourceType() {
        return this.randomTickResourceType;
    }
}

