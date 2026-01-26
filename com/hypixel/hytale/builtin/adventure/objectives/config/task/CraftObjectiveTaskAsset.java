/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.task;

import com.hypixel.hytale.builtin.adventure.objectives.config.task.CountObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.ObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.taskcondition.TaskConditionAsset;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import javax.annotation.Nonnull;

public class CraftObjectiveTaskAsset
extends CountObjectiveTaskAsset {
    public static final BuilderCodec<CraftObjectiveTaskAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(CraftObjectiveTaskAsset.class, CraftObjectiveTaskAsset::new, CountObjectiveTaskAsset.CODEC).append(new KeyedCodec<String>("ItemId", Codec.STRING), (objective, entityType) -> {
        objective.itemId = entityType;
    }, objective -> objective.itemId).addValidator(Validators.nonNull()).addValidator(Item.VALIDATOR_CACHE.getValidator()).add()).build();
    protected String itemId;

    public CraftObjectiveTaskAsset(String descriptionId, TaskConditionAsset[] taskConditions, Vector3i[] mapMarkers, int count, String itemId) {
        super(descriptionId, taskConditions, mapMarkers, count);
        this.itemId = itemId;
    }

    protected CraftObjectiveTaskAsset() {
    }

    @Override
    @Nonnull
    public ObjectiveTaskAsset.TaskScope getTaskScope() {
        return ObjectiveTaskAsset.TaskScope.PLAYER_AND_MARKER;
    }

    public String getItemId() {
        return this.itemId;
    }

    @Override
    protected boolean matchesAsset0(ObjectiveTaskAsset task) {
        if (!super.matchesAsset0(task)) {
            return false;
        }
        if (!(task instanceof CraftObjectiveTaskAsset)) {
            return false;
        }
        return ((CraftObjectiveTaskAsset)task).itemId.equals(this.itemId);
    }

    @Override
    @Nonnull
    public String toString() {
        return "CraftObjectiveTaskAsset{itemId='" + this.itemId + "'} " + super.toString();
    }
}

