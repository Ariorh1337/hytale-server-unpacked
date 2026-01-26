/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.movement.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.corecomponents.movement.BodyMotionLeave;
import com.hypixel.hytale.server.npc.corecomponents.movement.builders.BuilderBodyMotionFindBase;
import javax.annotation.Nonnull;

public class BuilderBodyMotionLeave
extends BuilderBodyMotionFindBase {
    protected final DoubleHolder distance = new DoubleHolder();

    public BuilderBodyMotionLeave() {
        super(false);
    }

    @Override
    @Nonnull
    public BodyMotionLeave build(@Nonnull BuilderSupport builderSupport) {
        return new BodyMotionLeave(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Leave place";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Get away from current position using path finding";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Experimental;
    }

    @Override
    @Nonnull
    public BuilderBodyMotionFindBase readConfig(@Nonnull JsonElement data) {
        super.readConfig(data);
        this.requireDouble(data, "Distance", this.distance, (DoubleValidator)DoubleSingleValidator.greater0(), BuilderDescriptorState.Experimental, "Minimum distance required", null);
        return this;
    }

    public double getDistance(@Nonnull BuilderSupport support) {
        return this.distance.get(support.getExecutionContext());
    }
}

