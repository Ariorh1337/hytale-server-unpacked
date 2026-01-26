/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.entity.filters.builders;

import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.corecomponents.entity.filters.EntityFilterAnd;
import com.hypixel.hytale.server.npc.corecomponents.entity.filters.builders.BuilderEntityFilterMany;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderEntityFilterAnd
extends BuilderEntityFilterMany {
    @Override
    @Nonnull
    public String getShortDescription() {
        return "Logical AND of a list of filters";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nullable
    public IEntityFilter build(@Nonnull BuilderSupport builderSupport) {
        Object filters = this.objectListHelper.build(builderSupport);
        if (filters.isEmpty()) {
            return null;
        }
        return new EntityFilterAnd((List<IEntityFilter>)filters);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }
}

