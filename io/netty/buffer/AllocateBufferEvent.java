/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractBufferEvent;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label(value="Buffer Allocation")
@Name(value="io.netty.AllocateBuffer")
@Description(value="Triggered when a buffer is allocated (or reallocated) from an allocator")
final class AllocateBufferEvent
extends AbstractBufferEvent {
    static final String NAME = "io.netty.AllocateBuffer";
    private static final AllocateBufferEvent INSTANCE = new AllocateBufferEvent();
    @Description(value="Is this chunk pooled, or is it a one-off allocation for this buffer?")
    public boolean chunkPooled;
    @Description(value="Is this buffer's chunk part of a thread-local magazine or arena?")
    public boolean chunkThreadLocal;

    AllocateBufferEvent() {
    }

    public static boolean isEventEnabled() {
        return INSTANCE.isEnabled();
    }
}

