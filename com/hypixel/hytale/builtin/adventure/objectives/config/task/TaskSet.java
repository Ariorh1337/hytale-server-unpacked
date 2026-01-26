/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.task;

import com.hypixel.hytale.builtin.adventure.objectives.config.task.ObjectiveTaskAsset;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import java.text.MessageFormat;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class TaskSet {
    public static final BuilderCodec<TaskSet> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(TaskSet.class, TaskSet::new).append(new KeyedCodec<String>("DescriptionId", Codec.STRING), (taskSet, s) -> {
        taskSet.descriptionId = s;
    }, taskSet -> taskSet.descriptionId).add()).append(new KeyedCodec<T[]>("Tasks", new ArrayCodec<ObjectiveTaskAsset>(ObjectiveTaskAsset.CODEC, ObjectiveTaskAsset[]::new)), (taskSet, objectiveTaskAssets) -> {
        taskSet.tasks = objectiveTaskAssets;
    }, taskSet -> taskSet.tasks).addValidator(Validators.nonEmptyArray()).add()).build();
    public static final String TASKSET_DESCRIPTION_KEY = "server.objectives.{0}.taskSet.{1}";
    protected String descriptionId;
    protected ObjectiveTaskAsset[] tasks;

    public TaskSet(String descriptionId, ObjectiveTaskAsset[] tasks) {
        this.descriptionId = descriptionId;
        this.tasks = tasks;
    }

    protected TaskSet() {
    }

    public String getDescriptionId() {
        return this.descriptionId;
    }

    @Nonnull
    public String getDescriptionKey(String objectiveId, int taskSetIndex) {
        if (this.descriptionId != null) {
            return this.descriptionId;
        }
        return MessageFormat.format(TASKSET_DESCRIPTION_KEY, objectiveId, taskSetIndex);
    }

    public ObjectiveTaskAsset[] getTasks() {
        return this.tasks;
    }

    @Nonnull
    public String toString() {
        return "TaskSet{descriptionId='" + this.descriptionId + "', tasks=" + Arrays.toString(this.tasks) + "}";
    }
}

