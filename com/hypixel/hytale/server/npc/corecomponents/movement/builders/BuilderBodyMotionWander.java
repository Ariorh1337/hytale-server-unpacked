/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.movement.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.movement.BodyMotionWander;
import com.hypixel.hytale.server.npc.corecomponents.movement.builders.BuilderBodyMotionWanderBase;
import javax.annotation.Nonnull;

public class BuilderBodyMotionWander
extends BuilderBodyMotionWanderBase {
    @Override
    @Nonnull
    public BodyMotionWander build(@Nonnull BuilderSupport builderSupport) {
        return new BodyMotionWander(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Random movement";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Random movement in short linear pieces.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderBodyMotionWanderBase readConfig(JsonElement data) {
        return this;
    }
}

