/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.lifecycle.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.corecomponents.lifecycle.ActionDespawn;
import javax.annotation.Nonnull;

public class BuilderActionDespawn
extends BuilderActionBase {
    protected boolean force;

    @Override
    @Nonnull
    public ActionDespawn build(BuilderSupport builderSupport) {
        return new ActionDespawn(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Trigger the NPC to despawn";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Trigger the NPC to start the despawning cycle. If the script contains a despawn sensor it will run that action/motion before removing.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionDespawn readConfig(@Nonnull JsonElement data) {
        this.getBoolean(data, "Force", (boolean b) -> {
            this.force = b;
        }, false, BuilderDescriptorState.Stable, "Force the NPC to remove automatically", null);
        return this;
    }

    public boolean isForced() {
        return this.force;
    }
}

