/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.audiovisual.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.animations.NPCAnimationSlot;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.EnumHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.corecomponents.audiovisual.SensorAnimation;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public class BuilderSensorAnimation
extends BuilderSensorBase {
    protected final EnumHolder<NPCAnimationSlot> animationSlot = new EnumHolder();
    protected final StringHolder animationId = new StringHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Check if a given animation is being played";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public Sensor build(@Nonnull BuilderSupport builderSupport) {
        return new SensorAnimation(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.requireEnum(data, "Slot", this.animationSlot, NPCAnimationSlot.class, BuilderDescriptorState.Stable, "The animation slot to check", null);
        this.requireString(data, "Animation", this.animationId, (StringValidator)StringNotEmptyValidator.get(), BuilderDescriptorState.Stable, "The animation ID to check for", null);
        return this;
    }

    public NPCAnimationSlot getAnimationSlot(@Nonnull BuilderSupport support) {
        return this.animationSlot.get(support.getExecutionContext());
    }

    public String getAnimationId(@Nonnull BuilderSupport support) {
        return this.animationId.get(support.getExecutionContext());
    }
}

