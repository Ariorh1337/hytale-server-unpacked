/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.movement.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.corecomponents.movement.BodyMotionWanderInRect;
import com.hypixel.hytale.server.npc.corecomponents.movement.builders.BuilderBodyMotionWanderBase;
import javax.annotation.Nonnull;

public class BuilderBodyMotionWanderInRect
extends BuilderBodyMotionWanderBase {
    protected double width;
    protected double depth;

    @Override
    @Nonnull
    public BodyMotionWanderInRect build(@Nonnull BuilderSupport builderSupport) {
        super.build(builderSupport);
        return new BodyMotionWanderInRect(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Random movement in rectangle around spawn position";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Random movement in short linear pieces inside rectangle around spawn position.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderBodyMotionWanderInRect readConfig(@Nonnull JsonElement data) {
        this.getDouble(data, "Width", (double w) -> {
            this.width = w;
        }, 10.0, (DoubleValidator)DoubleSingleValidator.greater0(), BuilderDescriptorState.Stable, "Rectangle width", null);
        this.getDouble(data, "Depth", (double d) -> {
            this.depth = d;
        }, 10.0, (DoubleValidator)DoubleSingleValidator.greater0(), BuilderDescriptorState.Stable, "Rectangle depth", null);
        return this;
    }

    public double getWidth() {
        return this.width;
    }

    public double getDepth() {
        return this.depth;
    }
}

