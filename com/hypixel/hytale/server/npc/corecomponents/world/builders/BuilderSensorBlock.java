/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.world.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.core.asset.type.blockset.config.BlockSet;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleRangeValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.BlockSetExistsValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.world.SensorBlock;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public class BuilderSensorBlock
extends BuilderSensorBase {
    protected final DoubleHolder range = new DoubleHolder();
    protected final DoubleHolder yRange = new DoubleHolder();
    protected final AssetHolder blockSet = new AssetHolder();
    protected final BooleanHolder pickRandom = new BooleanHolder();
    protected final BooleanHolder reserveBlock = new BooleanHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Checks for one of a set of blocks in the nearby area";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Checks for one of a set of blocks in the nearby area and caches the result until explicitly reset or the targeted block changes/is removed. All block sensors with the same sought blockset share the same targeted block once found";
    }

    @Override
    @Nonnull
    public Sensor build(@Nonnull BuilderSupport builderSupport) {
        return new SensorBlock(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Experimental;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.requireDouble(data, "Range", this.range, (DoubleValidator)DoubleRangeValidator.fromExclToIncl(0.0, Double.MAX_VALUE), BuilderDescriptorState.Stable, "The range to search for the blocks in", null);
        this.getDouble(data, "MaxHeight", this.yRange, 4.0, (DoubleValidator)DoubleRangeValidator.fromExclToIncl(0.0, Double.MAX_VALUE), BuilderDescriptorState.Stable, "The vertical range to search for the blocks in", null);
        this.requireAsset(data, "Blocks", this.blockSet, (AssetValidator)BlockSetExistsValidator.required(), BuilderDescriptorState.Stable, "The set of blocks to search for", null);
        this.getBoolean(data, "Random", this.pickRandom, false, BuilderDescriptorState.Stable, "Whether to pick at random from within the matched blocks or pick the closest", null);
        this.getBoolean(data, "Reserve", this.reserveBlock, false, BuilderDescriptorState.Stable, "Whether to reserve the found block to prevent other NPCs selecting it", null);
        this.provideFeature(Feature.Position);
        return this;
    }

    public double getRange(@Nonnull BuilderSupport support) {
        return this.range.get(support.getExecutionContext());
    }

    public double getYRange(@Nonnull BuilderSupport support) {
        return this.yRange.get(support.getExecutionContext());
    }

    public int getBlockSet(@Nonnull BuilderSupport support) {
        String key = this.blockSet.get(support.getExecutionContext());
        int index = BlockSet.getAssetMap().getIndex(key);
        if (index == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Unknown key! " + key);
        }
        return index;
    }

    public boolean isPickRandom(@Nonnull BuilderSupport support) {
        return this.pickRandom.get(support.getExecutionContext());
    }

    public boolean isReserveBlock(@Nonnull BuilderSupport support) {
        return this.reserveBlock.get(support.getExecutionContext());
    }
}

