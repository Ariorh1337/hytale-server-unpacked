/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.instructions.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.instructions.Instruction;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import com.hypixel.hytale.server.npc.instructions.builders.BuilderInstruction;
import it.unimi.dsi.fastutil.ints.IntSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderInstructionReference
extends BuilderInstruction {
    @Nullable
    protected IntSet internalDependencies;

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Prioritized instruction list that can be referenced from elsewhere in the file";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Prioritized instruction list that can be referenced from elsewhere in the file. Otherwise works like the default";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nullable
    public Instruction build(@Nonnull BuilderSupport builderSupport) {
        Instruction[] instructionList;
        Sensor sensor;
        if (!this.enabled.get(builderSupport.getExecutionContext())) {
            return null;
        }
        if (this.sensorBuilderObjectReferenceHelper.isPresent()) {
            sensor = this.getSensor(builderSupport);
            if (sensor == null) {
                return null;
            }
        } else {
            sensor = Sensor.NULL;
        }
        if (this.currentStateName != null) {
            builderSupport.pushCurrentStateName(this.currentStateName);
        }
        Instruction[] instructionArray = instructionList = this.hasNestedInstructions() ? this.getSteps(builderSupport) : null;
        if (!(instructionList != null || this.hasActions() || this.hasBodyMotion() || this.hasHeadMotion())) {
            if (this.currentStateName != null) {
                builderSupport.popCurrentStateName();
            }
            return null;
        }
        if (this.currentStateName != null) {
            builderSupport.popCurrentStateName();
        }
        return new Instruction(this, sensor, instructionList, builderSupport);
    }

    @Override
    public boolean excludeFromRegularBuilding() {
        return true;
    }

    @Override
    protected boolean requiresName() {
        return true;
    }

    @Override
    @Nullable
    public String getName() {
        return null;
    }

    @Override
    @Nonnull
    public Builder<Instruction> readConfig(@Nonnull JsonElement data) {
        if (!this.isCreatingDescriptor()) {
            this.internalReferenceResolver.setRecordDependencies();
        }
        this.getParameterBlock(data, BuilderDescriptorState.Stable, "The parameter block for defining variables", null);
        super.readConfig(data);
        this.cleanupParameters();
        if (!this.isCreatingDescriptor()) {
            this.internalDependencies = this.internalReferenceResolver.getRecordedDependenices();
            this.internalReferenceResolver.stopRecordingDependencies();
            int index = this.internalReferenceResolver.getOrCreateIndex(this.name);
            this.internalReferenceResolver.addBuilder(index, this);
        }
        return this;
    }

    @Nullable
    public IntSet getInternalDependencies() {
        return this.internalDependencies;
    }
}

