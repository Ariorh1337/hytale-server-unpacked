/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.task;

import com.hypixel.hytale.builtin.adventure.objectives.config.task.BlockTagOrItemIdField;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.CountObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.ObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.taskcondition.TaskConditionAsset;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;

public class GatherObjectiveTaskAsset
extends CountObjectiveTaskAsset {
    public static final BuilderCodec<GatherObjectiveTaskAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(GatherObjectiveTaskAsset.class, GatherObjectiveTaskAsset::new, CountObjectiveTaskAsset.CODEC).append(new KeyedCodec<BlockTagOrItemIdField>("BlockTagOrItemId", BlockTagOrItemIdField.CODEC), (gatherObjectiveTaskAsset, blockTagOrItemIdField) -> {
        gatherObjectiveTaskAsset.blockTagOrItemIdField = blockTagOrItemIdField;
    }, gatherObjectiveTaskAsset -> gatherObjectiveTaskAsset.blockTagOrItemIdField).addValidator(Validators.nonNull()).add()).build();
    protected BlockTagOrItemIdField blockTagOrItemIdField;

    public GatherObjectiveTaskAsset(String descriptionId, TaskConditionAsset[] taskConditions, Vector3i[] mapMarkers, int count, BlockTagOrItemIdField blockTagOrItemIdField) {
        super(descriptionId, taskConditions, mapMarkers, count);
        this.blockTagOrItemIdField = blockTagOrItemIdField;
    }

    protected GatherObjectiveTaskAsset() {
    }

    @Override
    @Nonnull
    public ObjectiveTaskAsset.TaskScope getTaskScope() {
        return ObjectiveTaskAsset.TaskScope.PLAYER_AND_MARKER;
    }

    public BlockTagOrItemIdField getBlockTagOrItemIdField() {
        return this.blockTagOrItemIdField;
    }

    @Override
    protected boolean matchesAsset0(ObjectiveTaskAsset task) {
        if (!super.matchesAsset0(task)) {
            return false;
        }
        if (!(task instanceof GatherObjectiveTaskAsset)) {
            return false;
        }
        return ((GatherObjectiveTaskAsset)task).blockTagOrItemIdField.equals(this.blockTagOrItemIdField);
    }

    @Override
    @Nonnull
    public String toString() {
        return "GatherObjectiveTaskAsset{blockTagOrItemIdTask=" + String.valueOf(this.blockTagOrItemIdField) + "} " + super.toString();
    }
}

