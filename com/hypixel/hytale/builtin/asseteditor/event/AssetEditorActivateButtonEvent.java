/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.asseteditor.event;

import com.hypixel.hytale.builtin.asseteditor.EditorClient;
import com.hypixel.hytale.builtin.asseteditor.event.EditorClientEvent;

public class AssetEditorActivateButtonEvent
extends EditorClientEvent<String> {
    private final String buttonId;

    public AssetEditorActivateButtonEvent(EditorClient editorClient, String buttonId) {
        super(editorClient);
        this.buttonId = buttonId;
    }

    public String getButtonId() {
        return this.buttonId;
    }
}

