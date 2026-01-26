/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.npcshop.npc.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.builtin.adventure.npcshop.npc.ActionOpenShop;
import com.hypixel.hytale.builtin.adventure.npcshop.npc.ShopExistsValidator;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import java.util.EnumSet;
import javax.annotation.Nonnull;

public class BuilderActionOpenShop
extends BuilderActionBase {
    protected final AssetHolder shopId = new AssetHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Open the shop UI for the current player";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public Action build(@Nonnull BuilderSupport builderSupport) {
        return new ActionOpenShop(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionOpenShop readConfig(@Nonnull JsonElement data) {
        this.requireAsset(data, "Shop", this.shopId, (AssetValidator)ShopExistsValidator.required(), BuilderDescriptorState.Stable, "The shop to open", null);
        this.requireInstructionType(EnumSet.of(InstructionType.Interaction));
        return this;
    }

    public String getShopId(@Nonnull BuilderSupport support) {
        return this.shopId.get(support.getExecutionContext());
    }
}

