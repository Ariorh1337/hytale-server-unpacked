/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.movement.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderBodyMotionBase;
import com.hypixel.hytale.server.npc.corecomponents.movement.BodyMotionMatchLook;
import javax.annotation.Nonnull;

public class BuilderBodyMotionMatchLook
extends BuilderBodyMotionBase {
    @Override
    @Nonnull
    public BodyMotionMatchLook build(BuilderSupport builderSupport) {
        return new BodyMotionMatchLook(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Make NPC body rotate to match look direction";
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
    public BuilderBodyMotionMatchLook readConfig(JsonElement data) {
        return this;
    }
}

