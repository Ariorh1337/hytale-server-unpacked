/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractBufferEvent;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Label(value="Buffer Deallocation")
@Name(value="io.netty.FreeBuffer")
@Description(value="Triggered when a buffer is freed from an allocator")
final class FreeBufferEvent
extends AbstractBufferEvent {
    private static final FreeBufferEvent INSTANCE = new FreeBufferEvent();
    static final String NAME = "io.netty.FreeBuffer";

    FreeBufferEvent() {
    }

    public static boolean isEventEnabled() {
        return INSTANCE.isEnabled();
    }
}

