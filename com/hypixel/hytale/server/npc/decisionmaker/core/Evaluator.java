/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.decisionmaker.core;

import com.hypixel.hytale.common.map.IWeightedElement;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.decisionmaker.core.EvaluationContext;
import com.hypixel.hytale.server.npc.decisionmaker.core.Option;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Evaluator<OptionType extends Option> {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static long NOT_USED = 0L;
    protected List<OptionHolder> options;

    public void initialise() {
        this.options.sort(Comparator.comparingDouble(OptionHolder::getWeightCoefficient).reversed());
        for (OptionHolder optionHolder : this.options) {
            ((Option)optionHolder.option).sortConditions();
        }
    }

    public void setupNPC(Role role) {
        for (OptionHolder optionHolder : this.options) {
            ((Option)optionHolder.option).setupNPC(role);
        }
    }

    public void setupNPC(Holder<EntityStore> holder) {
        for (OptionHolder optionHolder : this.options) {
            ((Option)optionHolder.option).setupNPC(holder);
        }
    }

    @Nullable
    public OptionHolder evaluate(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, CommandBuffer<EntityStore> commandBuffer, @Nonnull EvaluationContext context) {
        NPCEntity npcComponent = archetypeChunk.getComponent(index, NPCEntity.getComponentType());
        assert (npcComponent != null);
        UUIDComponent uuidComponent = archetypeChunk.getComponent(index, UUIDComponent.getComponentType());
        assert (uuidComponent != null);
        OptionHolder bestOption = null;
        double minimumWeight = context.getMinimumWeightCoefficient();
        int nonMatchingIndex = this.options.size();
        for (int i = 0; i < this.options.size(); ++i) {
            OptionHolder optionHolder = this.options.get(i);
            if (optionHolder.getWeightCoefficient() < minimumWeight) {
                nonMatchingIndex = i;
                break;
            }
            double utility = optionHolder.calculateUtility(index, archetypeChunk, commandBuffer, context);
            HytaleLogger.Api logContext = LOGGER.at(Level.FINE);
            if (logContext.isEnabled()) {
                logContext.log("%s with uuid %s: Scored option %s at %s", npcComponent.getRoleName(), uuidComponent.getUuid(), optionHolder.option, utility);
            }
            if (utility <= 0.0 || bestOption != null && !(utility > bestOption.utility)) continue;
            bestOption = optionHolder;
        }
        if (bestOption == null) {
            return null;
        }
        float predictability = context.getPredictability();
        if (predictability == 1.0f) {
            return bestOption;
        }
        double threshold = bestOption.utility * (double)predictability;
        double sum = 0.0;
        for (int i = 0; i < nonMatchingIndex; ++i) {
            OptionHolder optionHolder = this.options.get(i);
            if (!(optionHolder.utility >= threshold)) continue;
            sum += optionHolder.getTotalUtility(threshold);
        }
        double randomWeight = ThreadLocalRandom.current().nextDouble(sum);
        for (int i = 0; i < nonMatchingIndex; ++i) {
            OptionHolder optionHolder = this.options.get(i);
            if (optionHolder.utility < threshold || !((randomWeight = optionHolder.tryPick(randomWeight, threshold)) <= 0.0)) continue;
            bestOption = optionHolder;
            break;
        }
        return bestOption;
    }

    public abstract class OptionHolder
    implements IWeightedElement {
        protected final OptionType option;
        protected double utility;

        /*
         * WARNING - Possible parameter corruption
         */
        public OptionHolder(OptionType option) {
            this.option = option;
        }

        @Override
        public double getWeight() {
            return this.utility;
        }

        public double getWeightCoefficient() {
            return ((Option)this.option).getWeightCoefficient();
        }

        public OptionType getOption() {
            return this.option;
        }

        public double getTotalUtility(double threshold) {
            return this.utility;
        }

        public double tryPick(double currentWeight, double threshold) {
            return currentWeight - this.utility;
        }

        public abstract double calculateUtility(int var1, ArchetypeChunk<EntityStore> var2, CommandBuffer<EntityStore> var3, EvaluationContext var4);
    }
}

