/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.combat.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleRangeValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderBodyMotionBase;
import com.hypixel.hytale.server.npc.corecomponents.combat.BodyMotionAimCharge;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import javax.annotation.Nonnull;

public class BuilderBodyMotionAimCharge
extends BuilderBodyMotionBase {
    protected final DoubleHolder relativeTurnSpeed = new DoubleHolder();

    @Override
    @Nonnull
    public BodyMotion build(@Nonnull BuilderSupport builderSupport) {
        return new BodyMotionAimCharge(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Aim the NPC at a target position for performing a charge based on aiming information and ensure that the charge is possible before it's executed.";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderBodyMotionAimCharge readConfig(@Nonnull JsonElement data) {
        this.getDouble(data, "RelativeTurnSpeed", this.relativeTurnSpeed, 1.0, (DoubleValidator)DoubleRangeValidator.fromExclToIncl(0.0, 2.0), BuilderDescriptorState.Stable, "The relative turn speed modifier", null);
        this.requireFeature(Feature.AnyPosition);
        return this;
    }

    public double getRelativeTurnSpeed(@Nonnull BuilderSupport support) {
        return this.relativeTurnSpeed.get(support.getExecutionContext());
    }
}

