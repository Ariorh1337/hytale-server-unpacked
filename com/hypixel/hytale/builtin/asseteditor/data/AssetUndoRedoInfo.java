/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.asseteditor.data;

import com.hypixel.hytale.protocol.packets.asseteditor.JsonUpdateCommand;
import java.util.ArrayDeque;
import java.util.Deque;

public class AssetUndoRedoInfo {
    public final Deque<JsonUpdateCommand> undoStack = new ArrayDeque<JsonUpdateCommand>();
    public final Deque<JsonUpdateCommand> redoStack = new ArrayDeque<JsonUpdateCommand>();
}

