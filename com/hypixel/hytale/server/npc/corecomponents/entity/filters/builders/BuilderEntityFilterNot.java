/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.entity.filters.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectReferenceHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderEntityFilterWithToggle;
import com.hypixel.hytale.server.npc.corecomponents.entity.filters.EntityFilterNot;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.validators.NPCLoadTimeValidationHelper;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderEntityFilterNot
extends BuilderEntityFilterWithToggle {
    protected final BuilderObjectReferenceHelper<IEntityFilter> filter = new BuilderObjectReferenceHelper(IEntityFilter.class, this);

    @Override
    @Nullable
    public IEntityFilter build(@Nonnull BuilderSupport builderSupport) {
        IEntityFilter filter = this.getFilter(builderSupport);
        if (filter == null) {
            return null;
        }
        return new EntityFilterNot(filter);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Invert filter test";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    public void registerTags(@Nonnull Set<String> tags) {
        super.registerTags(tags);
        tags.add("logic");
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    @Nonnull
    public Builder<IEntityFilter> readConfig(@Nonnull JsonElement data) {
        this.requireObject(data, "Filter", this.filter, BuilderDescriptorState.Stable, "Filter to test", null, this.validationHelper);
        return this;
    }

    @Override
    public boolean validate(String configName, @Nonnull NPCLoadTimeValidationHelper validationHelper, @Nonnull ExecutionContext context, Scope globalScope, @Nonnull List<String> errors) {
        return super.validate(configName, validationHelper, context, globalScope, errors) & this.filter.validate(configName, validationHelper, this.builderManager, context, globalScope, errors);
    }

    @Nullable
    public IEntityFilter getFilter(@Nonnull BuilderSupport support) {
        return this.filter.build(support);
    }
}

