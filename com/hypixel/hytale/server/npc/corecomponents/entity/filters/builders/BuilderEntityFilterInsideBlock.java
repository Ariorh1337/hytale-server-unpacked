/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.entity.filters.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.core.asset.type.blockset.config.BlockSet;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.BlockSetExistsValidator;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderEntityFilterBase;
import com.hypixel.hytale.server.npc.corecomponents.entity.filters.EntityFilterInsideBlock;
import javax.annotation.Nonnull;

public class BuilderEntityFilterInsideBlock
extends BuilderEntityFilterBase {
    protected final AssetHolder blockSet = new AssetHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Matches if the entity is inside any of the blocks in the BlockSet";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public IEntityFilter build(@Nonnull BuilderSupport builderSupport) {
        return new EntityFilterInsideBlock(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<IEntityFilter> readConfig(@Nonnull JsonElement data) {
        this.requireAsset(data, "BlockSet", this.blockSet, (AssetValidator)BlockSetExistsValidator.required(), BuilderDescriptorState.Stable, "The BlockSet to match against", null);
        return this;
    }

    public int getBlockSet(@Nonnull BuilderSupport support) {
        String key = this.blockSet.get(support.getExecutionContext());
        int index = BlockSet.getAssetMap().getIndex(key);
        if (index == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Unknown key! " + key);
        }
        return index;
    }
}

