/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.instructions.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.BuilderValidationHelper;
import com.hypixel.hytale.server.npc.asset.builder.FeatureEvaluatorHelper;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.NumberArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleArrayValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSequenceValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNullOrNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.instructions.Instruction;
import com.hypixel.hytale.server.npc.instructions.InstructionRandomized;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import com.hypixel.hytale.server.npc.instructions.builders.BuilderInstruction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderInstructionRandomized
extends BuilderInstruction {
    public static final double[] DEFAULT_EXECUTION_RANGE = new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
    protected final BooleanHolder resetOnStateChange = new BooleanHolder();
    protected final NumberArrayHolder executeFor = new NumberArrayHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Randomised list of weighted instructions.";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Randomised list of weighted instructions. One will be selected at random and executed until the NPC state changes.";
    }

    @Override
    @Nullable
    public InstructionRandomized build(@Nonnull BuilderSupport builderSupport) {
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
        if (instructionList == null) {
            if (this.currentStateName != null) {
                builderSupport.popCurrentStateName();
            }
            return null;
        }
        if (this.currentStateName != null) {
            builderSupport.popCurrentStateName();
        }
        return new InstructionRandomized(this, sensor, instructionList, builderSupport);
    }

    @Override
    @Nonnull
    public Builder<Instruction> readConfig(@Nonnull JsonElement data) {
        FeatureEvaluatorHelper features = new FeatureEvaluatorHelper();
        BuilderValidationHelper helper = new BuilderValidationHelper(this.fileName, features, this.internalReferenceResolver, this.stateHelper, this.instructionContextHelper, this.extraInfo, this.evaluators, this.readErrors);
        this.increaseDepth();
        if (this.requiresName()) {
            this.requireString(data, "Name", (String v) -> {
                this.name = v;
            }, null, BuilderDescriptorState.Stable, "Name for referencing", null);
        } else {
            this.getString(data, "Name", (String v) -> {
                this.name = v;
            }, null, null, BuilderDescriptorState.Stable, "Optional name for descriptor", null);
        }
        this.getString(data, "Tag", (String v) -> {
            this.tag = v;
        }, null, (StringValidator)StringNullOrNotEmptyValidator.get(), BuilderDescriptorState.Experimental, "Internal identifier tag for debugging", null);
        this.getBoolean(data, "Enabled", this.enabled, true, BuilderDescriptorState.Stable, "Whether this step should be enabled on the NPC", null);
        this.getObject(data, "Sensor", this.sensorBuilderObjectReferenceHelper, BuilderDescriptorState.Stable, "Sensor for testing if step can be applied", "Sensor for testing if step can be applied. If not supplied, will always match", helper);
        features.lock();
        this.getArray(data, "Instructions", this.steps, null, BuilderDescriptorState.Stable, "List of weighted instructions to select from", null, new BuilderValidationHelper(this.fileName, null, this.internalReferenceResolver, this.stateHelper, this.instructionContextHelper, this.extraInfo, this.evaluators, this.readErrors));
        this.getBoolean(data, "Continue", (boolean v) -> {
            this.continueAfter = v;
        }, false, BuilderDescriptorState.WorkInProgress, "Continue after this step was executed", null);
        this.getDouble(data, "Weight", this.chance, 1.0, (DoubleValidator)DoubleSingleValidator.greater0(), BuilderDescriptorState.Stable, "Weighted chance of picking this step in a random selector", null);
        this.getBoolean(data, "TreeMode", (boolean v) -> {
            this.treeMode = v;
        }, false, BuilderDescriptorState.Stable, "Whether this step and its contents should be treated like a traditional behaviour tree.", "Whether this step and its contents should be treated like a traditional behaviour tree, i.e. will continue if all child steps fail");
        this.getBoolean(data, "InvertTreeModeResult", this.invertTreeModeResult, false, BuilderDescriptorState.Stable, "Whether or not to invert the result of TreeMode evaluation when passing up to parent TreeMode steps", null);
        this.getBoolean(data, "ResetOnStateChange", this.resetOnStateChange, true, BuilderDescriptorState.Stable, "Whether to reset when NPC state changes", null);
        this.getDoubleRange(data, "ExecuteFor", this.executeFor, DEFAULT_EXECUTION_RANGE, (DoubleArrayValidator)DoubleSequenceValidator.fromExclToInclWeaklyMonotonic(0.0, Double.MAX_VALUE), BuilderDescriptorState.Stable, "How long to execute the chosen step before picking another", null);
        this.decreaseDepth();
        this.validateBooleanImplicationAnyAntecedent(ANTECEDENT, new boolean[]{this.treeMode}, true, SUBSEQUENT, new boolean[]{this.continueAfter}, false);
        return this;
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    public boolean getResetOnStateChange(@Nonnull BuilderSupport support) {
        return this.resetOnStateChange.get(support.getExecutionContext());
    }

    public double[] getExecuteFor(@Nonnull BuilderSupport support) {
        return this.executeFor.get(support.getExecutionContext());
    }
}

