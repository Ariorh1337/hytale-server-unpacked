/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractBufferEvent;
import jdk.jfr.DataAmount;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label(value="Buffer Reallocation")
@Name(value="io.netty.ReallocateBuffer")
@Description(value="Triggered when a buffer is reallocated for resizing in an allocator. Will be followed by an AllocateBufferEvent")
final class ReallocateBufferEvent
extends AbstractBufferEvent {
    static final String NAME = "io.netty.ReallocateBuffer";
    private static final ReallocateBufferEvent INSTANCE = new ReallocateBufferEvent();
    @DataAmount
    @Description(value="Targeted buffer capacity")
    public int newCapacity;

    ReallocateBufferEvent() {
    }

    public static boolean isEventEnabled() {
        return INSTANCE.isEnabled();
    }
}

