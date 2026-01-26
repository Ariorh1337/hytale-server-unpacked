/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractChunkEvent;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label(value="Chunk Return")
@Name(value="io.netty.ReturnChunk")
@Description(value="Triggered when a memory chunk is prepared for re-use by an allocator")
final class ReturnChunkEvent
extends AbstractChunkEvent {
    static final String NAME = "io.netty.ReturnChunk";
    private static final ReturnChunkEvent INSTANCE = new ReturnChunkEvent();
    @Description(value="Was this chunk returned to its previous magazine?")
    public boolean returnedToMagazine;

    ReturnChunkEvent() {
    }

    public static boolean isEventEnabled() {
        return INSTANCE.isEnabled();
    }
}

