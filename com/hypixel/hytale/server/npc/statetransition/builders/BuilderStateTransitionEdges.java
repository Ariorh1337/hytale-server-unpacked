/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.statetransition.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.IntSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringArrayNoEmptyStringsValidator;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderStateTransitionEdges
extends BuilderBase<StateTransitionEdges> {
    @Nullable
    protected String[] fromStates;
    @Nullable
    protected String[] toStates;
    protected int[] fromStateIndices;
    protected int[] toStateIndices;
    protected int priority;
    protected final BooleanHolder enabled = new BooleanHolder();
    protected StateTransitionEdges builtStateTransitionEdges;

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Sets of from and to states defining state transitions";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public StateTransitionEdges build(BuilderSupport builderSupport) {
        if (this.builtStateTransitionEdges == null) {
            this.builtStateTransitionEdges = new StateTransitionEdges(this);
        }
        return this.builtStateTransitionEdges;
    }

    @Override
    @Nonnull
    public Class<StateTransitionEdges> category() {
        return StateTransitionEdges.class;
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    public boolean isEnabled(ExecutionContext context) {
        return this.enabled.get(context);
    }

    @Override
    @Nonnull
    public Builder<StateTransitionEdges> readConfig(@Nonnull JsonElement data) {
        int[] pos;
        this.getInt(data, "Priority", (int i) -> {
            this.priority = i;
        }, 0, IntSingleValidator.greaterEqual0(), BuilderDescriptorState.Stable, "Priority for the actions in this transition", null);
        this.requireStringArray(data, "From", o -> {
            this.fromStates = o;
        }, null, StringArrayNoEmptyStringsValidator.get(), BuilderDescriptorState.Stable, null, "A set of from states");
        this.requireStringArray(data, "To", o -> {
            this.toStates = o;
        }, null, StringArrayNoEmptyStringsValidator.get(), BuilderDescriptorState.Stable, null, "A set of to states");
        this.getBoolean(data, "Enabled", this.enabled, true, BuilderDescriptorState.Stable, "Whether this sensor should be enabled on the NPC", null);
        if (this.fromStates != null && this.toStates != null) {
            for (String state : this.fromStates) {
                for (String otherState : this.toStates) {
                    if (!state.equals(otherState)) continue;
                    this.addError(new IllegalStateException("State transition edge cannot be defined from a state to itself: " + state));
                }
            }
        }
        if (this.fromStates != null && this.fromStates.length > 0) {
            this.fromStateIndices = new int[this.fromStates.length];
            pos = new int[]{0};
            for (String state : this.fromStates) {
                this.registerStateRequirer(state, null, (ms, ss) -> {
                    int n = pos[0];
                    pos[0] = n + 1;
                    this.fromStateIndices[n] = ms;
                });
            }
            this.fromStates = null;
        }
        if (this.toStates != null && this.toStates.length > 0) {
            this.toStateIndices = new int[this.toStates.length];
            pos = new int[]{0};
            for (String state : this.toStates) {
                this.registerStateRequirer(state, null, (ms, ss) -> {
                    int n = pos[0];
                    pos[0] = n + 1;
                    this.toStateIndices[n] = ms;
                });
            }
            this.toStates = null;
        }
        return this;
    }

    public static class StateTransitionEdges {
        private final int priority;
        private final int[] fromStateIndices;
        private final int[] toStateIndices;

        private StateTransitionEdges(@Nonnull BuilderStateTransitionEdges builder) {
            this.priority = builder.priority;
            this.fromStateIndices = builder.fromStateIndices;
            this.toStateIndices = builder.toStateIndices;
        }

        public int getPriority() {
            return this.priority;
        }

        public int[] getFromStateIndices() {
            return this.fromStateIndices;
        }

        public int[] getToStateIndices() {
            return this.toStateIndices;
        }
    }
}

