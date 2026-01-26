/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.movement.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.corecomponents.movement.ActionRecomputePath;
import com.hypixel.hytale.server.npc.instructions.Action;
import javax.annotation.Nonnull;

public class BuilderActionRecomputePath
extends BuilderActionBase
implements Builder<Action> {
    @Override
    @Nonnull
    public ActionRecomputePath build(BuilderSupport builderSupport) {
        return new ActionRecomputePath(this);
    }

    @Nonnull
    public BuilderActionRecomputePath readConfig(JsonElement data) {
        return this;
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Force recomputation of path finder solution";
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
}

