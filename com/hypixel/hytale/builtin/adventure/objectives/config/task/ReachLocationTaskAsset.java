/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.task;

import com.hypixel.hytale.builtin.adventure.objectives.config.task.ObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.markers.reachlocation.ReachLocationMarkerAsset;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ReachLocationTaskAsset
extends ObjectiveTaskAsset {
    public static final BuilderCodec<ReachLocationTaskAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ReachLocationTaskAsset.class, ReachLocationTaskAsset::new, BASE_CODEC).append(new KeyedCodec<String>("TargetLocation", Codec.STRING), (reachLocationTaskAsset, vector3i) -> {
        reachLocationTaskAsset.targetLocationId = vector3i;
    }, reachLocationTaskAsset -> reachLocationTaskAsset.targetLocationId).addValidator(Validators.nonNull()).addValidator(ReachLocationMarkerAsset.VALIDATOR_CACHE.getValidator()).add()).build();
    protected String targetLocationId;

    @Override
    @Nonnull
    public ObjectiveTaskAsset.TaskScope getTaskScope() {
        return ObjectiveTaskAsset.TaskScope.PLAYER;
    }

    public String getTargetLocationId() {
        return this.targetLocationId;
    }

    @Override
    protected boolean matchesAsset0(ObjectiveTaskAsset task) {
        if (!(task instanceof ReachLocationTaskAsset)) {
            return false;
        }
        ReachLocationTaskAsset asset = (ReachLocationTaskAsset)task;
        return Objects.equals(asset.targetLocationId, this.targetLocationId);
    }

    @Override
    @Nonnull
    public String toString() {
        return "ReachLocationTaskAsset{targetLocationId=" + this.targetLocationId + "} " + super.toString();
    }
}

