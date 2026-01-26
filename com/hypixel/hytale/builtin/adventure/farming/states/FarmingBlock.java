/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.farming.states;

import com.hypixel.hytale.builtin.adventure.farming.FarmingPlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.time.Instant;
import javax.annotation.Nullable;

public class FarmingBlock
implements Component<ChunkStore> {
    public static final String DEFAULT_STAGE_SET = "Default";
    public static final BuilderCodec<FarmingBlock> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FarmingBlock.class, FarmingBlock::new).append(new KeyedCodec<String>("CurrentStageSet", Codec.STRING), (farmingBlock, currentStageSet) -> {
        farmingBlock.currentStageSet = currentStageSet;
    }, farmingBlock -> DEFAULT_STAGE_SET.equals(farmingBlock.currentStageSet) ? null : DEFAULT_STAGE_SET).add()).append(new KeyedCodec<Float>("GrowthProgress", Codec.FLOAT), (farmingBlock, growthProgress) -> {
        farmingBlock.growthProgress = growthProgress.floatValue();
    }, farmingBlock -> farmingBlock.growthProgress == 0.0f ? null : Float.valueOf(farmingBlock.growthProgress)).add()).append(new KeyedCodec("LastTickGameTime", Codec.INSTANT), (farmingBlock, lastTickGameTime) -> {
        farmingBlock.lastTickGameTime = lastTickGameTime;
    }, farmingBlock -> farmingBlock.lastTickGameTime).add()).append(new KeyedCodec<Integer>("Generation", Codec.INTEGER), (farmingBlock, generation) -> {
        farmingBlock.generation = generation;
    }, farmingBlock -> farmingBlock.generation == 0 ? null : Integer.valueOf(farmingBlock.generation)).add()).append(new KeyedCodec<String>("PreviousBlockType", Codec.STRING), (farmingBlock, previousBlockType) -> {
        farmingBlock.previousBlockType = previousBlockType;
    }, farmingBlock -> farmingBlock.previousBlockType).add()).append(new KeyedCodec<Float>("SpreadRate", Codec.FLOAT), (farmingBlock, spreadRate) -> {
        farmingBlock.spreadRate = spreadRate.floatValue();
    }, farmingBlock -> farmingBlock.spreadRate == 1.0f ? null : Float.valueOf(farmingBlock.spreadRate)).add()).append(new KeyedCodec<Integer>("Executions", Codec.INTEGER), (farmingBlock, executions) -> {
        farmingBlock.executions = executions;
    }, farmingBlock -> farmingBlock.executions == 0 ? null : Integer.valueOf(farmingBlock.executions)).add()).build();
    private String currentStageSet = "Default";
    private float growthProgress;
    private Instant lastTickGameTime;
    private int generation;
    private String previousBlockType;
    private float spreadRate = 1.0f;
    private int executions = 0;

    public static ComponentType<ChunkStore, FarmingBlock> getComponentType() {
        return FarmingPlugin.get().getFarmingBlockComponentType();
    }

    public FarmingBlock() {
    }

    public FarmingBlock(String currentStageSet, float growthProgress, Instant lastTickGameTime, int generation, String previousBlockType, float spreadRate, int executions) {
        this.currentStageSet = currentStageSet;
        this.growthProgress = growthProgress;
        this.lastTickGameTime = lastTickGameTime;
        this.generation = generation;
        this.previousBlockType = previousBlockType;
        this.spreadRate = spreadRate;
        this.executions = executions;
    }

    public String getCurrentStageSet() {
        return this.currentStageSet;
    }

    public void setCurrentStageSet(String currentStageSet) {
        this.currentStageSet = currentStageSet != null ? currentStageSet : DEFAULT_STAGE_SET;
    }

    public float getGrowthProgress() {
        return this.growthProgress;
    }

    public void setGrowthProgress(float growthProgress) {
        this.growthProgress = growthProgress;
    }

    public Instant getLastTickGameTime() {
        return this.lastTickGameTime;
    }

    public void setLastTickGameTime(Instant lastTickGameTime) {
        this.lastTickGameTime = lastTickGameTime;
    }

    public int getGeneration() {
        return this.generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public String getPreviousBlockType() {
        return this.previousBlockType;
    }

    public void setPreviousBlockType(String previousBlockType) {
        this.previousBlockType = previousBlockType;
    }

    public float getSpreadRate() {
        return this.spreadRate;
    }

    public void setSpreadRate(float spreadRate) {
        this.spreadRate = spreadRate;
    }

    public int getExecutions() {
        return this.executions;
    }

    public void setExecutions(int executions) {
        this.executions = executions;
    }

    @Override
    @Nullable
    public Component<ChunkStore> clone() {
        return new FarmingBlock(this.currentStageSet, this.growthProgress, this.lastTickGameTime, this.generation, this.previousBlockType, this.spreadRate, this.executions);
    }

    public String toString() {
        return "FarmingBlock{currentStageSet='" + this.currentStageSet + "', growthProgress=" + this.growthProgress + ", lastTickGameTime=" + String.valueOf(this.lastTickGameTime) + ", generation=" + this.generation + ", previousBlockType='" + this.previousBlockType + "', spreadRate=" + this.spreadRate + ", executions=" + this.executions + "}";
    }
}

