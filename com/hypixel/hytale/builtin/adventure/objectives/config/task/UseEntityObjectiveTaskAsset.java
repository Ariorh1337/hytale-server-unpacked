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
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UseEntityObjectiveTaskAsset
extends CountObjectiveTaskAsset {
    public static final BuilderCodec<UseEntityObjectiveTaskAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(UseEntityObjectiveTaskAsset.class, UseEntityObjectiveTaskAsset::new, CountObjectiveTaskAsset.CODEC).append(new KeyedCodec<String>("TaskId", Codec.STRING), (useEntityObjectiveTaskAsset, s) -> {
        useEntityObjectiveTaskAsset.taskId = s;
    }, useEntityObjectiveTaskAsset -> useEntityObjectiveTaskAsset.taskId).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<String>("AnimationIdToPlay", Codec.STRING), (useEntityObjectiveTaskAsset, s) -> {
        useEntityObjectiveTaskAsset.animationIdToPlay = s;
    }, useEntityObjectiveTaskAsset -> useEntityObjectiveTaskAsset.animationIdToPlay).add()).append(new KeyedCodec<DialogOptions>("Dialog", DialogOptions.CODEC), (useEntityObjectiveTask, dialogOptions) -> {
        useEntityObjectiveTask.dialogOptions = dialogOptions;
    }, useEntityObjectiveTask -> useEntityObjectiveTask.dialogOptions).add()).build();
    protected String taskId;
    protected String animationIdToPlay;
    protected DialogOptions dialogOptions;

    public UseEntityObjectiveTaskAsset(String descriptionId, TaskConditionAsset[] taskConditions, Vector3i[] mapMarkers, int count, String taskId, String animationIdToPlay, DialogOptions dialogOptions) {
        super(descriptionId, taskConditions, mapMarkers, count);
        this.taskId = taskId;
        this.animationIdToPlay = animationIdToPlay;
        this.dialogOptions = dialogOptions;
    }

    protected UseEntityObjectiveTaskAsset() {
    }

    @Override
    @Nonnull
    public ObjectiveTaskAsset.TaskScope getTaskScope() {
        return ObjectiveTaskAsset.TaskScope.PLAYER_AND_MARKER;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public String getAnimationIdToPlay() {
        return this.animationIdToPlay;
    }

    public DialogOptions getDialogOptions() {
        return this.dialogOptions;
    }

    @Override
    protected boolean matchesAsset0(ObjectiveTaskAsset task) {
        if (!super.matchesAsset0(task)) {
            return false;
        }
        if (!(task instanceof UseEntityObjectiveTaskAsset)) {
            return false;
        }
        UseEntityObjectiveTaskAsset asset = (UseEntityObjectiveTaskAsset)task;
        if (!Objects.equals(asset.animationIdToPlay, this.animationIdToPlay)) {
            return false;
        }
        if (!Objects.equals(asset.dialogOptions, this.dialogOptions)) {
            return false;
        }
        return asset.taskId.equals(this.taskId);
    }

    @Override
    @Nonnull
    public String toString() {
        return "UseEntityObjectiveTaskAsset{taskId='" + this.taskId + "', animationIdToPlay='" + this.animationIdToPlay + "', dialogOptions=" + String.valueOf(this.dialogOptions) + "} " + super.toString();
    }

    public static class DialogOptions {
        @Nonnull
        public static BuilderCodec<DialogOptions> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DialogOptions.class, DialogOptions::new).append(new KeyedCodec<String>("EntityNameKey", Codec.STRING), (dialogOptions, s) -> {
            dialogOptions.entityNameKey = s;
        }, dialogOptions -> dialogOptions.entityNameKey).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<String>("DialogKey", Codec.STRING), (dialogOptions, s) -> {
            dialogOptions.dialogKey = s;
        }, dialogOptions -> dialogOptions.dialogKey).addValidator(Validators.nonNull()).add()).build();
        protected String entityNameKey;
        protected String dialogKey;

        public DialogOptions(String entityNameKey, String dialogKey) {
            this.entityNameKey = entityNameKey;
            this.dialogKey = dialogKey;
        }

        protected DialogOptions() {
        }

        public String getEntityNameKey() {
            return this.entityNameKey;
        }

        public String getDialogKey() {
            return this.dialogKey;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            DialogOptions that = (DialogOptions)o;
            if (!this.entityNameKey.equals(that.entityNameKey)) {
                return false;
            }
            return this.dialogKey.equals(that.dialogKey);
        }

        public int hashCode() {
            int result = this.entityNameKey.hashCode();
            result = 31 * result + this.dialogKey.hashCode();
            return result;
        }

        @Nonnull
        public String toString() {
            return "DialogOptions{entityNameKey='" + this.entityNameKey + "', dialogKey='" + this.dialogKey + "'}";
        }
    }
}

