/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.audiovisual.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.corecomponents.audiovisual.ActionDisplayName;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import javax.annotation.Nonnull;

public class BuilderActionDisplayName
extends BuilderActionBase {
    protected final StringHolder displayName = new StringHolder();

    @Override
    @Nonnull
    public ActionDisplayName build(@Nonnull BuilderSupport builderSupport) {
        return new ActionDisplayName(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Set display name.";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Set the name displayed above NPC";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionDisplayName readConfig(@Nonnull JsonElement data) {
        this.requireString(data, "DisplayName", this.displayName, null, BuilderDescriptorState.Stable, "Name to display above NPC", null);
        return this;
    }

    public String getDisplayName(@Nonnull BuilderSupport support) {
        return this.displayName.get(support.getExecutionContext());
    }
}

