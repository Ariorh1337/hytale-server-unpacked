/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.npcobjectives.assets;

import com.hypixel.hytale.builtin.adventure.npcobjectives.assets.KillObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.ObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.taskcondition.TaskConditionAsset;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.codec.validation.validator.ArrayValidator;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.spawning.assets.spawnmarker.config.SpawnMarker;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class KillSpawnMarkerObjectiveTaskAsset
extends KillObjectiveTaskAsset {
    public static final BuilderCodec<KillSpawnMarkerObjectiveTaskAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(KillSpawnMarkerObjectiveTaskAsset.class, KillSpawnMarkerObjectiveTaskAsset::new, KillObjectiveTaskAsset.CODEC).append(new KeyedCodec<Float>("Radius", Codec.FLOAT), (killSpawnMarkerObjectiveTaskAsset, aFloat) -> {
        killSpawnMarkerObjectiveTaskAsset.radius = aFloat.floatValue();
    }, killSpawnMarkerObjectiveTaskAsset -> Float.valueOf(killSpawnMarkerObjectiveTaskAsset.radius)).addValidator(Validators.greaterThan(Float.valueOf(0.0f))).add()).append(new KeyedCodec<T[]>("SpawnMarkerIds", Codec.STRING_ARRAY), (killSpawnMarkerObjectiveTaskAsset, s) -> {
        killSpawnMarkerObjectiveTaskAsset.spawnMarkerIds = s;
    }, killSpawnMarkerObjectiveTaskAsset -> killSpawnMarkerObjectiveTaskAsset.spawnMarkerIds).addValidator(Validators.nonEmptyArray()).addValidator(SpawnMarker.VALIDATOR_CACHE.getArrayValidator()).addValidator(new ArrayValidator<String>((o, results) -> {
        SpawnMarker spawnMarker = SpawnMarker.getAssetMap().getAsset((String)o);
        if (spawnMarker != null && !spawnMarker.isManualTrigger()) {
            results.fail("SpawnMarker '" + o + "' can't be triggered manually!");
        }
    })).add()).build();
    protected String[] spawnMarkerIds;
    protected float radius = 1.0f;

    public KillSpawnMarkerObjectiveTaskAsset(String descriptionId, TaskConditionAsset[] taskConditions, Vector3i[] mapMarkers, int count, String npcGroupId, String[] spawnMarkerIds, float radius) {
        super(descriptionId, taskConditions, mapMarkers, count, npcGroupId);
        this.spawnMarkerIds = spawnMarkerIds;
        this.radius = radius;
    }

    protected KillSpawnMarkerObjectiveTaskAsset() {
    }

    @Nonnull
    public String[] getSpawnMarkerIds() {
        return this.spawnMarkerIds;
    }

    public float getRadius() {
        return this.radius;
    }

    @Override
    protected boolean matchesAsset0(ObjectiveTaskAsset task) {
        if (!super.matchesAsset0(task)) {
            return false;
        }
        if (!(task instanceof KillSpawnMarkerObjectiveTaskAsset)) {
            return false;
        }
        KillSpawnMarkerObjectiveTaskAsset killSpawnMarkerObjectiveTaskAsset = (KillSpawnMarkerObjectiveTaskAsset)task;
        if (!Arrays.equals(killSpawnMarkerObjectiveTaskAsset.spawnMarkerIds, this.spawnMarkerIds)) {
            return false;
        }
        return killSpawnMarkerObjectiveTaskAsset.radius == this.radius;
    }

    @Override
    @Nonnull
    public String toString() {
        return "KillSpawnMarkerObjectiveTaskAsset{spawnMarkerIds=" + Arrays.toString(this.spawnMarkerIds) + ", radius=" + this.radius + "} " + super.toString();
    }
}

