/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractChunkEvent;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name(value="io.netty.AllocateChunk")
@Label(value="Chunk Allocation")
@Description(value="Triggered when a new memory chunk is allocated for an allocator")
final class AllocateChunkEvent
extends AbstractChunkEvent {
    static final String NAME = "io.netty.AllocateChunk";
    private static final AllocateChunkEvent INSTANCE = new AllocateChunkEvent();
    @Description(value="Is this chunk pooled, or is it a one-off allocation for a single buffer?")
    public boolean pooled;
    @Description(value="Is this chunk part of a thread-local magazine or arena?")
    public boolean threadLocal;

    AllocateChunkEvent() {
    }

    public static boolean isEventEnabled() {
        return INSTANCE.isEnabled();
    }
}

