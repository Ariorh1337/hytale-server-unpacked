/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.npccombatactionevaluator.conditions;

import com.hypixel.hytale.builtin.npccombatactionevaluator.memory.TargetMemory;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.decisionmaker.core.EvaluationContext;
import com.hypixel.hytale.server.npc.decisionmaker.core.conditions.base.ScaledCurveCondition;
import javax.annotation.Nonnull;

public class TargetMemoryCountCondition
extends ScaledCurveCondition {
    public static final EnumCodec<TargetType> TARGET_TYPE_CODEC = new EnumCodec<TargetType>(TargetType.class).documentKey(TargetType.All, "All known targets.").documentKey(TargetType.Friendly, "Known friendly targets.").documentKey(TargetType.Hostile, "Known hostile targets.");
    public static final BuilderCodec<TargetMemoryCountCondition> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(TargetMemoryCountCondition.class, TargetMemoryCountCondition::new, ScaledCurveCondition.ABSTRACT_CODEC).documentation("A scaled curve condition that returns a utility value based on the number of known targets in the memory.")).appendInherited(new KeyedCodec<TargetType>("TargetType", TARGET_TYPE_CODEC), (condition, e) -> {
        condition.targetType = e;
    }, condition -> condition.targetType, (condition, parent) -> {
        condition.targetType = parent.targetType;
    }).documentation("The type of targets to count.").add()).build();
    protected static final ComponentType<EntityStore, TargetMemory> TARGET_MEMORY_COMPONENT_TYPE = TargetMemory.getComponentType();
    protected TargetType targetType = TargetType.Hostile;

    @Override
    protected double getInput(int selfIndex, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, Ref<EntityStore> target, CommandBuffer<EntityStore> commandBuffer, EvaluationContext context) {
        TargetMemory memory = archetypeChunk.getComponent(selfIndex, TARGET_MEMORY_COMPONENT_TYPE);
        return switch (this.targetType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 2 -> memory.getKnownFriendlies().size() + memory.getKnownHostiles().size();
            case 0 -> memory.getKnownHostiles().size();
            case 1 -> memory.getKnownFriendlies().size();
        };
    }

    private static enum TargetType {
        Hostile,
        Friendly,
        All;

    }
}

