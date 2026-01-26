/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab.selection.buffer;

import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBuffer;

public interface PrefabBufferSerializer<T> {
    public T serialize(PrefabBuffer var1);
}

