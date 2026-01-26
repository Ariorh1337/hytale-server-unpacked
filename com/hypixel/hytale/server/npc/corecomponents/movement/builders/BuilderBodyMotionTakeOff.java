/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.movement.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderBodyMotionBase;
import com.hypixel.hytale.server.npc.corecomponents.movement.BodyMotionTakeOff;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import com.hypixel.hytale.server.npc.movement.controllers.MotionControllerFly;
import com.hypixel.hytale.server.npc.movement.controllers.MotionControllerWalk;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.validators.NPCLoadTimeValidationHelper;
import java.util.List;
import javax.annotation.Nonnull;

public class BuilderBodyMotionTakeOff
extends BuilderBodyMotionBase {
    protected double jumpSpeed = 1.0;

    @Override
    @Nonnull
    public BodyMotion build(BuilderSupport builderSupport) {
        return new BodyMotionTakeOff(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Switch NPC from walking to flying motion controller";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Experimental;
    }

    @Override
    @Nonnull
    public Builder<BodyMotion> readConfig(@Nonnull JsonElement data) {
        this.getDouble(data, "JumpSpeed", (double v) -> {
            this.jumpSpeed = v;
        }, 0.0, (DoubleValidator)DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.Experimental, "Speed to jump off", null);
        return this;
    }

    @Override
    public boolean validate(String configName, @Nonnull NPCLoadTimeValidationHelper validationHelper, ExecutionContext context, Scope globalScope, @Nonnull List<String> errors) {
        boolean result = super.validate(configName, validationHelper, context, globalScope, errors);
        validationHelper.requireMotionControllerType(MotionControllerWalk.class);
        validationHelper.requireMotionControllerType(MotionControllerFly.class);
        return result;
    }

    public double getJumpSpeed() {
        return this.jumpSpeed;
    }
}

