/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.task;

import com.hypixel.hytale.builtin.adventure.objectives.config.taskcondition.TaskConditionAsset;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.math.vector.Vector3i;
import java.text.MessageFormat;
import java.util.Arrays;
import javax.annotation.Nonnull;

public abstract class ObjectiveTaskAsset {
    public static final CodecMapCodec<ObjectiveTaskAsset> CODEC = new CodecMapCodec("Type");
    public static final BuilderCodec<ObjectiveTaskAsset> BASE_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.abstractBuilder(ObjectiveTaskAsset.class).append(new KeyedCodec<String>("DescriptionId", Codec.STRING), (objectiveTaskAsset, s) -> {
        objectiveTaskAsset.descriptionId = s;
    }, objectiveTaskAsset -> objectiveTaskAsset.descriptionId).add()).append(new KeyedCodec<T[]>("TaskConditions", new ArrayCodec<TaskConditionAsset>(TaskConditionAsset.CODEC, TaskConditionAsset[]::new)), (useBlockObjectiveTaskAsset, inventoryConditions) -> {
        useBlockObjectiveTaskAsset.taskConditions = inventoryConditions;
    }, useBlockObjectiveTaskAsset -> useBlockObjectiveTaskAsset.taskConditions).add()).append(new KeyedCodec<T[]>("MapMarkers", new ArrayCodec<Vector3i>(Vector3i.CODEC, Vector3i[]::new)), (taskAsset, vector3is) -> {
        taskAsset.mapMarkers = vector3is;
    }, taskAsset -> taskAsset.mapMarkers).add()).build();
    public static final String TASK_DESCRIPTION_KEY = "server.objectives.{0}.taskSet.{1}.task.{2}";
    protected String descriptionId;
    protected TaskConditionAsset[] taskConditions;
    protected Vector3i[] mapMarkers;
    private String defaultDescriptionId;

    public ObjectiveTaskAsset(String descriptionId, TaskConditionAsset[] taskConditions, Vector3i[] mapMarkers) {
        this.descriptionId = descriptionId;
        this.taskConditions = taskConditions;
        this.mapMarkers = mapMarkers;
    }

    protected ObjectiveTaskAsset() {
    }

    public String getDescriptionId() {
        return this.descriptionId;
    }

    @Nonnull
    public String getDescriptionKey(String objectiveId, int taskSetIndex, int taskIndex) {
        if (this.descriptionId != null) {
            return this.descriptionId;
        }
        if (this.defaultDescriptionId == null) {
            this.defaultDescriptionId = MessageFormat.format(TASK_DESCRIPTION_KEY, objectiveId, taskSetIndex, taskIndex);
        }
        return this.defaultDescriptionId;
    }

    public TaskConditionAsset[] getTaskConditions() {
        return this.taskConditions;
    }

    public Vector3i[] getMapMarkers() {
        return this.mapMarkers;
    }

    public abstract TaskScope getTaskScope();

    public boolean matchesAsset(@Nonnull ObjectiveTaskAsset task) {
        if (!Arrays.equals(task.taskConditions, this.taskConditions)) {
            return false;
        }
        if (!Arrays.equals(task.mapMarkers, this.mapMarkers)) {
            return false;
        }
        if (!task.getClass().equals(this.getClass())) {
            return false;
        }
        return this.matchesAsset0(task);
    }

    protected abstract boolean matchesAsset0(ObjectiveTaskAsset var1);

    @Nonnull
    public String toString() {
        return "ObjectiveTaskAsset{descriptionId='" + this.descriptionId + "', taskConditions=" + Arrays.toString(this.taskConditions) + ", mapMarkers=" + Arrays.toString(this.mapMarkers) + "}";
    }

    public static enum TaskScope {
        PLAYER,
        MARKER,
        PLAYER_AND_MARKER;


        public boolean isTaskPossibleForMarker() {
            return this == MARKER || this == PLAYER_AND_MARKER;
        }

        public boolean isTaskPossibleForPlayer() {
            return this == PLAYER || this == PLAYER_AND_MARKER;
        }
    }
}

