/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.audiovisual.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.ModelExistsValidator;
import com.hypixel.hytale.server.npc.corecomponents.audiovisual.ActionAppearance;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import javax.annotation.Nonnull;

public class BuilderActionAppearance
extends BuilderActionBase {
    protected String appearance;

    @Override
    @Nonnull
    public ActionAppearance build(BuilderSupport builderSupport) {
        return new ActionAppearance(this);
    }

    @Nonnull
    public BuilderActionAppearance readConfig(@Nonnull JsonElement data) {
        this.requireAsset(data, "Appearance", (String s) -> {
            this.appearance = s;
        }, (AssetValidator)ModelExistsValidator.required(), BuilderDescriptorState.Stable, "Model name to use", null);
        return this;
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Set model displayed for NPC";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Change model of NPC to given appearance.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    public String getAppearance() {
        return this.appearance;
    }
}

