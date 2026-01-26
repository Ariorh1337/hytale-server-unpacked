/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.events;

import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import javax.annotation.Nonnull;

public abstract class ChunkEvent
implements IEvent<String> {
    @Nonnull
    private final WorldChunk chunk;

    public ChunkEvent(@Nonnull WorldChunk chunk) {
        this.chunk = chunk;
    }

    public WorldChunk getChunk() {
        return this.chunk;
    }

    @Nonnull
    public String toString() {
        return "ChunkEvent{chunk=" + String.valueOf(this.chunk) + "}";
    }
}

