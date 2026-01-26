/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.gameplay;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.gameplay.SleepConfig;
import javax.annotation.Nonnull;

public class WorldConfig {
    @Nonnull
    public static final BuilderCodec<WorldConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(WorldConfig.class, WorldConfig::new).append(new KeyedCodec<Boolean>("AllowBlockBreaking", Codec.BOOLEAN), (worldConfig, o) -> {
        worldConfig.allowBlockBreaking = o;
    }, worldConfig -> worldConfig.allowBlockBreaking).add()).append(new KeyedCodec<Boolean>("AllowBlockGathering", Codec.BOOLEAN), (worldConfig, o) -> {
        worldConfig.allowBlockGathering = o;
    }, worldConfig -> worldConfig.allowBlockGathering).add()).append(new KeyedCodec<Boolean>("AllowBlockPlacement", Codec.BOOLEAN), (worldConfig, o) -> {
        worldConfig.allowBlockPlacement = o;
    }, worldConfig -> worldConfig.allowBlockPlacement).add()).append(new KeyedCodec<Double>("BlockPlacementFragilityTimer", Codec.DOUBLE), (worldConfig, d) -> {
        worldConfig.blockPlacementFragilityTimer = d.floatValue();
    }, worldConfig -> worldConfig.blockPlacementFragilityTimer).documentation("The timer, in seconds, that blocks have after placement during which they are fragile and can be broken instantly").add()).append(new KeyedCodec<Integer>("DaytimeDurationSeconds", Codec.INTEGER), (worldConfig, i) -> {
        worldConfig.daytimeDurationSeconds = i;
    }, worldConfig -> worldConfig.daytimeDurationSeconds).documentation("The number of real-world seconds it takes for the day to pass (from sunrise to sunset)").add()).append(new KeyedCodec<Integer>("NighttimeDurationSeconds", Codec.INTEGER), (worldConfig, i) -> {
        worldConfig.nighttimeDurationSeconds = i;
    }, worldConfig -> worldConfig.nighttimeDurationSeconds).documentation("The number of real-world seconds it takes for the night to pass (from sunset to sunrise)").add()).append(new KeyedCodec<Integer>("TotalMoonPhases", Codec.INTEGER), (worldConfig, i) -> {
        worldConfig.totalMoonPhases = i;
    }, o -> o.totalMoonPhases).add()).append(new KeyedCodec<SleepConfig>("Sleep", SleepConfig.CODEC), (worldConfig, sleepConfig) -> {
        worldConfig.sleepConfig = sleepConfig;
    }, o -> o.sleepConfig).documentation("Configurations related to sleeping in this world (in beds)").add()).build();
    public static final int DEFAULT_TOTAL_DAY_DURATION_SECONDS = 2880;
    public static final int DEFAULT_DAYTIME_DURATION_SECONDS = 1728;
    public static final int DEFAULT_NIGHTTIME_DURATION_SECONDS = 1728;
    protected boolean allowBlockBreaking = true;
    protected boolean allowBlockGathering = true;
    protected boolean allowBlockPlacement = true;
    protected int daytimeDurationSeconds = 1728;
    protected int nighttimeDurationSeconds = 1728;
    private int totalMoonPhases = 5;
    protected float blockPlacementFragilityTimer;
    private SleepConfig sleepConfig = SleepConfig.DEFAULT;

    public boolean isBlockBreakingAllowed() {
        return this.allowBlockBreaking;
    }

    public boolean isBlockGatheringAllowed() {
        return this.allowBlockGathering;
    }

    public boolean isBlockPlacementAllowed() {
        return this.allowBlockPlacement;
    }

    public int getDaytimeDurationSeconds() {
        return this.daytimeDurationSeconds;
    }

    public int getNighttimeDurationSeconds() {
        return this.nighttimeDurationSeconds;
    }

    public int getTotalMoonPhases() {
        return this.totalMoonPhases;
    }

    public float getBlockPlacementFragilityTimer() {
        return this.blockPlacementFragilityTimer;
    }

    public SleepConfig getSleepConfig() {
        return this.sleepConfig;
    }
}

