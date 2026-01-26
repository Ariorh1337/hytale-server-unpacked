/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.asseteditor.event;

import com.hypixel.hytale.builtin.asseteditor.EditorClient;
import com.hypixel.hytale.builtin.asseteditor.event.EditorClientEvent;

public class AssetEditorUpdateWeatherPreviewLockEvent
extends EditorClientEvent<Void> {
    private final boolean locked;

    public AssetEditorUpdateWeatherPreviewLockEvent(EditorClient editorClient, boolean locked) {
        super(editorClient);
        this.locked = locked;
    }

    public boolean isLocked() {
        return this.locked;
    }
}

