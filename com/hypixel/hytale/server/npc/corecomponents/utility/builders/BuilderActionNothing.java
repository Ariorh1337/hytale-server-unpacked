/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.utility.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.corecomponents.utility.ActionNothing;
import javax.annotation.Nonnull;

public class BuilderActionNothing
extends BuilderActionBase {
    @Override
    @Nonnull
    public ActionNothing build(BuilderSupport builderSupport) {
        return new ActionNothing(this);
    }

    @Nonnull
    public BuilderActionNothing readConfig(JsonElement data) {
        return this;
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Do nothing";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Do nothing. Used often as placeholder.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }
}

