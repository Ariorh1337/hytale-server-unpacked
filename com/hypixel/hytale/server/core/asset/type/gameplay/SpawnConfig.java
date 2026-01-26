/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.gameplay;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpawnConfig {
    @Nonnull
    public static final BuilderCodec<SpawnConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SpawnConfig.class, SpawnConfig::new).appendInherited(new KeyedCodec<T[]>("FirstSpawnParticles", new ArrayCodec<WorldParticle>(WorldParticle.CODEC, WorldParticle[]::new)), (o, v) -> {
        o.firstSpawnParticles = v;
    }, o -> o.firstSpawnParticles, (o, p) -> {
        o.firstSpawnParticles = p.firstSpawnParticles;
    }).add()).appendInherited(new KeyedCodec<T[]>("SpawnParticles", new ArrayCodec<WorldParticle>(WorldParticle.CODEC, WorldParticle[]::new)), (o, v) -> {
        o.spawnParticles = v;
    }, o -> o.spawnParticles, (o, p) -> {
        o.spawnParticles = p.spawnParticles;
    }).add()).build();
    protected WorldParticle[] firstSpawnParticles;
    protected WorldParticle[] spawnParticles;

    @Nullable
    public WorldParticle[] getFirstSpawnParticles() {
        return this.firstSpawnParticles;
    }

    @Nullable
    public WorldParticle[] getSpawnParticles() {
        return this.spawnParticles;
    }
}

