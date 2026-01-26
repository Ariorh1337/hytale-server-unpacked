/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractChunkEvent;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label(value="Chunk Free")
@Name(value="io.netty.FreeChunk")
@Description(value="Triggered when a memory chunk is freed from an allocator")
final class FreeChunkEvent
extends AbstractChunkEvent {
    static final String NAME = "io.netty.FreeChunk";
    private static final FreeChunkEvent INSTANCE = new FreeChunkEvent();
    @Description(value="Was this chunk pooled, or was it a one-off allocation for a single buffer?")
    public boolean pooled;

    FreeChunkEvent() {
    }

    public static boolean isEventEnabled() {
        return INSTANCE.isEnabled();
    }
}

