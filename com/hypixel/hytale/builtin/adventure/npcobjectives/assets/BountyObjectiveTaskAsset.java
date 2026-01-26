/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.npcobjectives.assets;

import com.hypixel.hytale.builtin.adventure.objectives.config.task.ObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.taskcondition.TaskConditionAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.worldlocationproviders.WorldLocationProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.Objects;
import javax.annotation.Nonnull;

public class BountyObjectiveTaskAsset
extends ObjectiveTaskAsset {
    public static final BuilderCodec<BountyObjectiveTaskAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BountyObjectiveTaskAsset.class, BountyObjectiveTaskAsset::new, ObjectiveTaskAsset.BASE_CODEC).append(new KeyedCodec<String>("NpcId", Codec.STRING), (bountyObjectiveTaskAsset, s) -> {
        bountyObjectiveTaskAsset.npcId = s;
    }, bountyObjectiveTaskAsset -> bountyObjectiveTaskAsset.npcId).add()).append(new KeyedCodec<WorldLocationProvider>("WorldLocationCondition", WorldLocationProvider.CODEC), (bountyObjectiveTaskAsset, worldLocationCondition) -> {
        bountyObjectiveTaskAsset.worldLocationProvider = worldLocationCondition;
    }, bountyObjectiveTaskAsset -> bountyObjectiveTaskAsset.worldLocationProvider).addValidator(Validators.nonNull()).add()).build();
    protected String npcId;
    protected WorldLocationProvider worldLocationProvider;

    public BountyObjectiveTaskAsset(String descriptionId, TaskConditionAsset[] taskConditions, Vector3i[] mapMarkers, String npcId, WorldLocationProvider worldLocationProvider) {
        super(descriptionId, taskConditions, mapMarkers);
        this.npcId = npcId;
        this.worldLocationProvider = worldLocationProvider;
    }

    protected BountyObjectiveTaskAsset() {
    }

    @Override
    @Nonnull
    public ObjectiveTaskAsset.TaskScope getTaskScope() {
        return ObjectiveTaskAsset.TaskScope.PLAYER;
    }

    public String getNpcId() {
        return this.npcId;
    }

    public WorldLocationProvider getWorldLocationProvider() {
        return this.worldLocationProvider;
    }

    @Override
    protected boolean matchesAsset0(ObjectiveTaskAsset task) {
        if (!(task instanceof BountyObjectiveTaskAsset)) {
            return false;
        }
        BountyObjectiveTaskAsset asset = (BountyObjectiveTaskAsset)task;
        if (!Objects.equals(asset.npcId, this.npcId)) {
            return false;
        }
        return Objects.equals(asset.worldLocationProvider, this.worldLocationProvider);
    }

    @Override
    @Nonnull
    public String toString() {
        return "BountyObjectiveTaskAsset{npcId='" + this.npcId + "', worldLocationCondition=" + String.valueOf(this.worldLocationProvider) + "} " + super.toString();
    }
}

