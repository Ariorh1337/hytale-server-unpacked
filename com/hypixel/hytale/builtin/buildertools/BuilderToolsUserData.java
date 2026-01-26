/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderToolsUserData
implements Component<EntityStore> {
    public static final String ID = "BuilderTools";
    private static final String SELECTION_HISTORY_KEY = "SelectionHistory";
    private static final String SELECTION_HISTORY_DOC = "Controls whether changes to the block selection box are recorded in the undo/redo history.";
    public static final BuilderCodec<BuilderToolsUserData> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(BuilderToolsUserData.class, BuilderToolsUserData::new).append(new KeyedCodec<Boolean>("SelectionHistory", Codec.BOOLEAN), BuilderToolsUserData::setRecordSelectionHistory, BuilderToolsUserData::isRecordingSelectionHistory).addValidator(Validators.nonNull()).documentation("Controls whether changes to the block selection box are recorded in the undo/redo history.").add()).build();
    private boolean selectionHistory = true;

    @Nonnull
    public static BuilderToolsUserData get(@Nonnull Player player) {
        BuilderToolsUserData userData = player.toHolder().getComponent(BuilderToolsUserData.getComponentType());
        if (userData == null) {
            return new BuilderToolsUserData();
        }
        return userData;
    }

    public static ComponentType<EntityStore, BuilderToolsUserData> getComponentType() {
        return BuilderToolsPlugin.get().getUserDataComponentType();
    }

    public boolean isRecordingSelectionHistory() {
        return this.selectionHistory;
    }

    public void setRecordSelectionHistory(boolean selectionHistory) {
        this.selectionHistory = selectionHistory;
    }

    @Nonnull
    public String toString() {
        return "BuilderToolsUserData{selectionHistory=" + this.selectionHistory + "}";
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BuilderToolsUserData that = (BuilderToolsUserData)o;
        return this.selectionHistory == that.selectionHistory;
    }

    public int hashCode() {
        return this.selectionHistory ? 1 : 0;
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        BuilderToolsUserData settings = new BuilderToolsUserData();
        settings.selectionHistory = this.selectionHistory;
        return settings;
    }
}

