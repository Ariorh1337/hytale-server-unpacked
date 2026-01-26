/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.completion;

import com.hypixel.hytale.builtin.adventure.objectives.Objective;
import com.hypixel.hytale.builtin.adventure.objectives.completion.ObjectiveCompletion;
import com.hypixel.hytale.builtin.adventure.objectives.config.completion.ClearObjectiveItemsCompletionAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.completion.ObjectiveCompletionAsset;
import com.hypixel.hytale.builtin.adventure.objectives.interactions.StartObjectiveInteraction;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.UUID;
import javax.annotation.Nonnull;

public class ClearObjectiveItemsCompletion
extends ObjectiveCompletion {
    public ClearObjectiveItemsCompletion(@Nonnull ObjectiveCompletionAsset asset) {
        super(asset);
    }

    @Override
    @Nonnull
    public ClearObjectiveItemsCompletionAsset getAsset() {
        return (ClearObjectiveItemsCompletionAsset)super.getAsset();
    }

    @Override
    public void handle(@Nonnull Objective objective, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        objective.forEachParticipant((participantReference, objectiveUuid) -> {
            Entity patt0$temp = EntityUtils.getEntity(participantReference, componentAccessor);
            if (patt0$temp instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)patt0$temp;
                CombinedItemContainer inventory = livingEntity.getInventory().getCombinedHotbarFirst();
                for (short i = 0; i < inventory.getCapacity(); i = (short)(i + 1)) {
                    UUID savedObjectiveUuid;
                    ItemStack itemStack = inventory.getItemStack(i);
                    if (itemStack == null || !objectiveUuid.equals(savedObjectiveUuid = itemStack.getFromMetadataOrNull(StartObjectiveInteraction.OBJECTIVE_UUID))) continue;
                    inventory.removeItemStackFromSlot(i);
                }
            }
        }, objective.getObjectiveUUID());
    }

    @Override
    @Nonnull
    public String toString() {
        return "ClearObjectiveItemsCompletion{} " + super.toString();
    }
}

