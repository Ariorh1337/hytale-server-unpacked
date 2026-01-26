/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.migrations;

import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;

public interface Migration {
    public void run(WorldChunk var1);
}

