/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.entity.filters.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.ComponentContext;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleRangeValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderEntityFilterBase;
import com.hypixel.hytale.server.npc.corecomponents.entity.filters.EntityFilterSpotsMe;
import com.hypixel.hytale.server.npc.util.ViewTest;
import javax.annotation.Nonnull;

public class BuilderEntityFilterSpotsMe
extends BuilderEntityFilterBase {
    protected float viewAngle;
    protected boolean testLineOfSight;
    protected ViewTest viewTest;

    @Override
    @Nonnull
    public EntityFilterSpotsMe build(BuilderSupport builderSupport) {
        return new EntityFilterSpotsMe(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Checks if the entity can view the NPC in a given view sector or cone and without obstruction.";
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

    @Override
    @Nonnull
    public Builder<IEntityFilter> readConfig(@Nonnull JsonElement data) {
        this.getFloat(data, "ViewAngle", (float f) -> {
            this.viewAngle = f * ((float)Math.PI / 180);
        }, 90.0f, (DoubleValidator)DoubleRangeValidator.between(0.0, 360.0), BuilderDescriptorState.Stable, "Angle used for view sector and view cone test", null);
        this.getEnum(data, "ViewTest", (E e) -> {
            this.viewTest = e;
        }, ViewTest.class, ViewTest.VIEW_SECTOR, BuilderDescriptorState.Stable, "Check if the entity is in the view cone, view sector, or neither", null);
        this.getBoolean(data, "TestLineOfSight", (boolean b) -> {
            this.testLineOfSight = b;
        }, true, BuilderDescriptorState.Stable, "Check if view to the npc is not obstructed", null);
        this.requireContext(InstructionType.Any, ComponentContext.NotSelfEntitySensor);
        return this;
    }

    public float getViewAngle() {
        return this.viewAngle;
    }

    public boolean testLineOfSight() {
        return this.testLineOfSight;
    }

    public ViewTest getViewTest() {
        return this.viewTest;
    }
}

