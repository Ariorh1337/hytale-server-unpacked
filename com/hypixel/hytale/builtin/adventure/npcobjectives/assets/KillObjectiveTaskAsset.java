/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.npcobjectives.assets;

import com.hypixel.hytale.builtin.adventure.objectives.config.task.CountObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.ObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.taskcondition.TaskConditionAsset;
import com.hypixel.hytale.builtin.tagset.config.NPCGroup;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;

public class KillObjectiveTaskAsset
extends CountObjectiveTaskAsset {
    public static final BuilderCodec<KillObjectiveTaskAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(KillObjectiveTaskAsset.class, KillObjectiveTaskAsset::new, CountObjectiveTaskAsset.CODEC).append(new KeyedCodec<String>("NPCGroupId", Codec.STRING), (objective, entityType) -> {
        objective.npcGroupId = entityType;
    }, objective -> objective.npcGroupId).addValidator(Validators.nonNull()).addValidator(NPCGroup.VALIDATOR_CACHE.getValidator()).add()).build();
    protected String npcGroupId;

    public KillObjectiveTaskAsset(String descriptionId, TaskConditionAsset[] taskConditions, Vector3i[] mapMarkers, int count, String npcGroupId) {
        super(descriptionId, taskConditions, mapMarkers, count);
        this.npcGroupId = npcGroupId;
    }

    protected KillObjectiveTaskAsset() {
    }

    @Override
    @Nonnull
    public ObjectiveTaskAsset.TaskScope getTaskScope() {
        return ObjectiveTaskAsset.TaskScope.PLAYER_AND_MARKER;
    }

    public String getNpcGroupId() {
        return this.npcGroupId;
    }

    @Override
    protected boolean matchesAsset0(ObjectiveTaskAsset task) {
        if (!super.matchesAsset0(task)) {
            return false;
        }
        if (!(task instanceof KillObjectiveTaskAsset)) {
            return false;
        }
        return ((KillObjectiveTaskAsset)task).npcGroupId.equals(this.npcGroupId);
    }

    @Override
    @Nonnull
    public String toString() {
        return "KillObjectiveTaskAsset{npcGroupId='" + this.npcGroupId + "'} " + super.toString();
    }
}

