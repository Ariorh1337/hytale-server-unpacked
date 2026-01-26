/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab.selection.buffer;

import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferDeserializer;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferSerializer;

public interface PrefabBufferCodec<T>
extends PrefabBufferSerializer<T>,
PrefabBufferDeserializer<T> {
}

