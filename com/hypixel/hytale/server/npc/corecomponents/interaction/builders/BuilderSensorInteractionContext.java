/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.interaction.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.interaction.SensorInteractionContext;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import java.util.EnumSet;
import javax.annotation.Nonnull;

public class BuilderSensorInteractionContext
extends BuilderSensorBase {
    protected final StringHolder interactionContext = new StringHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Checks whether the currently iterated player in the interaction instruction has interacted with this NPC in the given context";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public Sensor build(@Nonnull BuilderSupport builderSupport) {
        return new SensorInteractionContext(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.requireString(data, "Context", this.interactionContext, (StringValidator)StringNotEmptyValidator.get(), BuilderDescriptorState.Stable, "The context of the interaction", null);
        this.requireInstructionType(EnumSet.of(InstructionType.Interaction));
        return this;
    }

    public String getInteractionContext(@Nonnull BuilderSupport support) {
        return this.interactionContext.get(support.getExecutionContext());
    }
}

