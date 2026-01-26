/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.npccombatactionevaluator.corecomponents.builders;

import com.hypixel.hytale.builtin.npccombatactionevaluator.corecomponents.CombatTargetCollector;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ISensorEntityCollector;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import javax.annotation.Nonnull;

public class BuilderCombatTargetCollector
extends BuilderBase<ISensorEntityCollector> {
    @Override
    @Nonnull
    public ISensorEntityCollector build(BuilderSupport builderSupport) {
        return new CombatTargetCollector();
    }

    @Override
    @Nonnull
    public Class<ISensorEntityCollector> category() {
        return ISensorEntityCollector.class;
    }

    @Override
    public boolean isEnabled(ExecutionContext context) {
        return true;
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "A collector which processes matched friendly and hostile targets and adds them to the NPC's short-term combat memory.";
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
}

