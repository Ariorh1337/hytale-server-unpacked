/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.lifecycle.builders;

import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.corecomponents.lifecycle.ActionDie;
import javax.annotation.Nonnull;

public class BuilderActionDie
extends BuilderActionBase {
    @Override
    @Nonnull
    public ActionDie build(BuilderSupport builderSupport) {
        return new ActionDie(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Kill the NPC";
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

