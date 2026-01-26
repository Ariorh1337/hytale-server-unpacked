/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.world.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.core.asset.type.blockset.config.BlockSet;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectReferenceHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.BlockSetExistsValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.world.SensorBlockType;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderSensorBlockType
extends BuilderSensorBase {
    protected final BuilderObjectReferenceHelper<Sensor> sensor = new BuilderObjectReferenceHelper(Sensor.class, this);
    protected final AssetHolder blockSet = new AssetHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Checks if the block at the given position matches the provided block set";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nullable
    public Sensor build(@Nonnull BuilderSupport builderSupport) {
        Sensor sensor = this.getSensor(builderSupport);
        if (sensor == null) {
            return null;
        }
        return new SensorBlockType(this, builderSupport, sensor);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.requireObject(data, "Sensor", this.sensor, BuilderDescriptorState.Stable, "Sensor to wrap", null, this.validationHelper);
        this.requireAsset(data, "BlockSet", this.blockSet, (AssetValidator)BlockSetExistsValidator.required(), BuilderDescriptorState.Stable, "Block set to check against", null);
        return this;
    }

    @Nullable
    public Sensor getSensor(@Nonnull BuilderSupport support) {
        return this.sensor.build(support);
    }

    public int getBlockSet(@Nonnull BuilderSupport support) {
        String key = this.blockSet.get(support.getExecutionContext());
        int index = BlockSet.getAssetMap().getIndex(key);
        if (index == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Unknown key! " + key);
        }
        return index;
    }
}

