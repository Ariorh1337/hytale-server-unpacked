/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.flock.corecomponents.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.flock.corecomponents.BodyMotionFlock;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderBodyMotionBase;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import javax.annotation.Nonnull;

public class BuilderBodyMotionFlock
extends BuilderBodyMotionBase {
    @Override
    @Nonnull
    public BodyMotionFlock build(BuilderSupport builderSupport) {
        return new BodyMotionFlock(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Flocking - WIP";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Flocking - WIP";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Experimental;
    }

    @Override
    @Nonnull
    public Builder<BodyMotion> readConfig(JsonElement data) {
        return this;
    }
}

